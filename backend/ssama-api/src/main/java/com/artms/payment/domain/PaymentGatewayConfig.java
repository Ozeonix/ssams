package com.artms.payment.domain;

import com.artms.shared.domain.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

/**
 * Per-tenant gateway configuration (eSewa, Khalti, etc.).
 * Secrets are NOT stored here – they come from environment variables.
 * This stores non-secret config: merchant_id (non-sensitive), base URLs, environment flag.
 */
@Entity
@Table(name = "payment_gateway_config",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "gateway_code"}))
@Getter
@Setter
@NoArgsConstructor
public class PaymentGatewayConfig extends TenantBaseEntity {

    @Column(name = "gateway_code", nullable = false, length = 64)
    private String gatewayCode;

    @Column(name = "display_name", nullable = false, length = 128)
    private String displayName;

    @Column(name = "environment", nullable = false, length = 32)
    private String environment = "SANDBOX";

    /** Non-secret merchant ID (e.g. eSewa service code like EPAYTEST) */
    @Column(name = "merchant_id", length = 256)
    private String merchantId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config_json", columnDefinition = "jsonb")
    private Map<String, Object> configJson;

    @Column(name = "is_active", nullable = false)
    private boolean active = false;
}
