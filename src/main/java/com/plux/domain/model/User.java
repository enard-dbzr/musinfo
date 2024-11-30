package com.plux.domain.model;

import java.util.UUID;

public record User(
        UUID id,
        String name,
        UserRole role
) {}
