package com.artms.tenant.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "tenant")
@Getter
@Setter
@NoArgsConstructor
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 64)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private TenantStatus status = TenantStatus.ACTIVE;

    @Column(name = "timezone", nullable = false, length = 64)
    private String timezone = "Asia/Kathmandu";

    @Column(name = "locale", nullable = false, length = 16)
    private String locale = "en";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "branding", columnDefinition = "jsonb")
    private Map<String, Object> branding;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "modules", columnDefinition = "jsonb")
    private Map<String, Boolean> modules;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "settings", columnDefinition = "jsonb")
    private Map<String, Object> settings;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public boolean isActive() {
        return TenantStatus.ACTIVE == this.status;
    }
}
