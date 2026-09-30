package com.artms.payment.domain;

import com.artms.shared.domain.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Fee structure: amount for a fee category per academic year + optional program/class restriction.
 * NULL program_id or class_section_id means the fee applies to all.
 */
@Entity
@Table(name = "fee_structure")
@Getter
@Setter
@NoArgsConstructor
public class FeeStructure extends TenantBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_category_id", nullable = false)
    private FeeCategory feeCategory;

    @Column(name = "academic_year_id")
    private UUID academicYearId;

    @Column(name = "program_id")
    private UUID programId;

    @Column(name = "class_section_id")
    private UUID classSectionId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "late_fee_per_day", precision = 8, scale = 2)
    private BigDecimal lateFeePerDay = BigDecimal.ZERO;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_by")
    private UUID createdBy;
}
