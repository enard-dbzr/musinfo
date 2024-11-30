package com.plux.domain.model;

import java.time.Duration;

public record Track(
        Integer id,
        String title,
        Duration duration
) {
}
