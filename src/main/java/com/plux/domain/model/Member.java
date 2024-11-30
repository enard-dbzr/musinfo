package com.plux.domain.model;

import java.util.Date;

public record Member(
        Integer id,
        String name,
        String displayName,
        Date birthday,
        String countryCode
) {}
