package com.plux.domain.model;

public record Label(
        Integer id,
        String name,
        String description,
        String address,
        String contactInfo
) {
}
