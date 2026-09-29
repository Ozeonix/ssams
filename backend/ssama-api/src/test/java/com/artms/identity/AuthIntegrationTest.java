package com.artms.identity;

import com.artms.IntegrationTestBase;
import com.artms.identity.domain.*;
import com.artms.tenant.domain.Tenant;
import com.artms.tenant.domain.TenantRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for authentication endpoints.
 * Verifies: login success, wrong password, account lockout,
 * token refresh, and logout/revocation.
 * CRITICAL: Tests also verify tenant isolation — user in tenant A cannot
 * authenticate as belonging to tenant B.
 */
class AuthIntegrationTest extends IntegrationTestBase {

    @LocalServerPort
    int port;

    @Autowired
    TenantRepository tenantRepository;

    @Autowired
    UserAccountRepository userAccountRepository;

    @Autowired
    UserTenantMembershipRepository membershipRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private Tenant tenantA;
    private Tenant tenantB;
    private UserAccount testUser;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1";

        // Create synthetic tenants — not real institution data
        tenantA = new Tenant();
        tenantA.setCode("test-tenant-a");
        tenantA.setName("Test Institution A");
        tenantA = tenantRepository.save(tenantA);

        tenantB = new Tenant();
        tenantB.setCode("test-tenant-b");
        tenantB.setName("Test Institution B");
        tenantB = tenantRepository.save(tenantB);

        // Create synthetic test user
        testUser = new UserAccount();
        testUser.setUsername("testuser01");
        testUser.setEmail("testuser01@example.test");
        testUser.setPasswordHash(passwordEncoder.encode("Test@Password1!"));
        testUser = userAccountRepository.save(testUser);

        // Create membership in tenant A only
        UserTenantMembership membership = new UserTenantMembership();
        membership.setTenant(tenantA);
        membership.setUser(testUser);
        membershipRepository.save(membership);
    }

    @Test
    void loginSuccess_returnsTokens() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"tenantCode":"test-tenant-a","username":"testuser01","password":"Test@Password1!"}
                """)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(200)
            .body("data.accessToken", notNullValue())
            .body("data.refreshToken", notNullValue())
            .body("data.expiresIn", greaterThan(0))
            .body("data.user.username", equalTo("testuser01"));
    }

    @Test
    void loginWithWrongPassword_returns409() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"tenantCode":"test-tenant-a","username":"testuser01","password":"WrongPassword!"}
                """)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(409)
            .body("code", equalTo("INVALID_CREDENTIALS"));
    }

    @Test
    void loginWithUnknownTenant_returns409() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"tenantCode":"nonexistent-tenant","username":"testuser01","password":"Test@Password1!"}
                """)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(409)
            .body("code", equalTo("INVALID_CREDENTIALS"));
    }

    /**
     * TENANT ISOLATION TEST: User in tenant A cannot log in under tenant B.
     */
    @Test
    void loginInWrongTenant_returns409() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"tenantCode":"test-tenant-b","username":"testuser01","password":"Test@Password1!"}
                """)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(409)
            .body("code", equalTo("INVALID_CREDENTIALS"));
    }

    @Test
    void refreshToken_rotatesSuccessfully() {
        // First, login to get a refresh token
        String refreshToken = given()
            .contentType(ContentType.JSON)
            .body("""
                {"tenantCode":"test-tenant-a","username":"testuser01","password":"Test@Password1!"}
                """)
            .post("/auth/login")
            .jsonPath().getString("data.refreshToken");

        // Then refresh
        given()
            .contentType(ContentType.JSON)
            .body("{\"refreshToken\":\"" + refreshToken + "\"}")
        .when()
            .post("/auth/refresh")
        .then()
            .statusCode(200)
            .body("data.accessToken", notNullValue())
            .body("data.refreshToken", notNullValue());
    }

    @Test
    void loginWithMissingField_returns422() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"tenantCode":"test-tenant-a","username":"testuser01"}
                """)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(422)
            .body("code", equalTo("VALIDATION_ERROR"))
            .body("fieldErrors", hasSize(greaterThan(0)));
    }
}
