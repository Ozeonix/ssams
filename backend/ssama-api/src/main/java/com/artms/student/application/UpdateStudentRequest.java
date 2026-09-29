package com.artms.student.application;

public record UpdateStudentRequest(
    String firstName,
    String middleName,
    String lastName,
    String phone,
    String email,
    String address
) {}
