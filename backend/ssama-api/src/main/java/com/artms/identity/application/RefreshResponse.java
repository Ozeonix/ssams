package com.artms.identity.application;

public record RefreshResponse(String accessToken, String refreshToken, long expiresIn) {}
