package com.artms.student.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "student_guardian")
@Getter
@Setter
@NoArgsConstructor
public class StudentGuardian {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "relationship", length = 64)
    private String relationship; // FATHER, MOTHER, GUARDIAN, SIBLING, OTHER

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "is_primary", nullable = false)
    private boolean primary = false;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
