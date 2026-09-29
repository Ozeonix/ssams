package com.artms.identity.infrastructure;

import com.artms.identity.domain.*;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.artms.shared.tenant.TenantContext;

/**
 * JWT authentication filter.
 * Extracts Bearer token, validates it, loads membership+permissions from DB,
 * and populates SecurityContext and TenantContext.
 *
 * Tenant ID is ALWAYS derived from the validated JWT claim, never from the request body.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserTenantMembershipRepository membershipRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                chain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);
            if (!jwtService.isValid(token)) {
                chain.doFilter(request, response);
                return;
            }

            Claims claims = jwtService.parseAndValidate(token);
            UUID userId = jwtService.extractUserId(claims);
            UUID tenantId = jwtService.extractTenantId(claims);

            Optional<UserTenantMembership> membershipOpt =
                    membershipRepository.findActiveWithRolesAndPermissions(userId, tenantId);

            if (membershipOpt.isEmpty()) {
                log.debug("No active membership for user {} in tenant {}", userId, tenantId);
                chain.doFilter(request, response);
                return;
            }

            UserTenantMembership membership = membershipOpt.get();

            if (!membership.getUser().isActive()) {
                log.debug("User {} is not active", userId);
                chain.doFilter(request, response);
                return;
            }

            // Build authorities from permissions across all roles
            List<GrantedAuthority> authorities = membership.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(perm -> (GrantedAuthority) new SimpleGrantedAuthority("PERM_" + perm.getCode()))
                    .collect(Collectors.toList());

            // Add role authorities as well for convenience
            membership.getRoles().forEach(role ->
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode())));

            ArtmsPrincipal principal = new ArtmsPrincipal(
                    userId, tenantId, jwtService.extractUsername(claims), membership.getId());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Set tenant context for this thread
            TenantContext.set(tenantId, userId);

            // Set MDC for structured logging
            MDC.put("tenantId", tenantId.toString());
            MDC.put("userId", userId.toString());

        } catch (Exception e) {
            log.debug("JWT filter error: {}", e.getMessage());
        }

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
            MDC.remove("tenantId");
            MDC.remove("userId");
        }
    }
}
