package com.artms.payment.domain;

import com.artms.shared.domain.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Fee category defined by the institution (tuition, exam, lab, transport, etc.).
 * Institution-configurable – no hard-coded categories.
 */
@Entity
@Table(name = "fee_category",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "code"}))
@Getter
@Setter
@NoArgsConstructor
public class FeeCategory extends TenantBaseEntity {

    @Column(name = "code", nullable = false, length = 64)
    private String code;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Column(name = "description", length = 512)
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
