package com.artms.student.domain;

import com.artms.shared.domain.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Student domain entity.
 * Linked to a user_account for app login.
 * Personal identifiers use configured admission/registration numbers, not hard-coded.
 */
@Entity
@Table(name = "student",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "admission_no"}))
@Getter
@Setter
@NoArgsConstructor
public class Student extends TenantBaseEntity {

    @Column(name = "admission_no", nullable = false, length = 64)
    private String admissionNo;

    @Column(name = "registration_no", length = 64)
    private String registrationNo;

    @Column(name = "symbol_no", length = 64)
    private String symbolNo;

    @Column(name = "first_name", nullable = false, length = 128)
    private String firstName;

    @Column(name = "middle_name", length = 128)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 128)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 16)
    private Gender gender;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "photo_url", length = 512)
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private StudentStatus status = StudentStatus.ACTIVE;

    @Column(name = "user_id")
    private UUID userId;

    @Version
    @Column(name = "version", nullable = false)
    private int version;

    public String getFullName() {
        if (middleName != null && !middleName.isBlank()) {
            return firstName + " " + middleName + " " + lastName;
        }
        return firstName + " " + lastName;
    }
}
