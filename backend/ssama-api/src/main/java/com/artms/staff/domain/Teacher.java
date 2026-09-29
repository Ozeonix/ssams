package com.artms.staff.domain;

import com.artms.shared.domain.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "teacher",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "user_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Teacher extends TenantBaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "employee_code", length = 64)
    private String employeeCode;

    @Column(name = "first_name", nullable = false, length = 128)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 128)
    private String lastName;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "designation", length = 128)
    private String designation;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "email", length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private TeacherStatus status = TeacherStatus.ACTIVE;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
