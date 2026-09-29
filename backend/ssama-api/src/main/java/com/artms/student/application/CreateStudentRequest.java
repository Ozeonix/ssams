package com.artms.student.application;

import com.artms.student.domain.Gender;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateStudentRequest(
    @NotBlank String admissionNo,
    @NotBlank String firstName,
    String middleName,
    @NotBlank String lastName,
    LocalDate dateOfBirth,
    Gender gender,
    String phone,
    String email,
    String address,
    String registrationNo,
    String symbolNo
) {}
