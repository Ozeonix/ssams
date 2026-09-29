package com.artms.attendance.domain;

import com.artms.shared.domain.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "attendance_session",
    uniqueConstraints = @UniqueConstraint(columnNames = {"class_group_id", "subject_id", "session_date", "period"}))
@Getter
@Setter
@NoArgsConstructor
public class AttendanceSession extends TenantBaseEntity {

    @Column(name = "class_group_id", nullable = false)
    private UUID classGroupId;

    @Column(name = "subject_id")
    private UUID subjectId;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "period", length = 32)
    private String period;

    @Column(name = "teacher_id")
    private UUID teacherId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private AttendanceSessionStatus status = AttendanceSessionStatus.DRAFT;

    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AttendanceRecord> records = new ArrayList<>();
}
