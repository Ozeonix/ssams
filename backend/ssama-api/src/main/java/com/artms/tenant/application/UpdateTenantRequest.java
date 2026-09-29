package com.artms.tenant.application;

import java.util.Map;

public record UpdateTenantRequest(
    String name,
    String timezone,
    String locale,
    Map<String, Object> branding,
    Map<String, Object> settings,
    Map<String, Boolean> modules
) {}
