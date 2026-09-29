package com.artms.identity.application;

import java.util.UUID;

public record UserSummary(UUID id, String username, String email) {}
