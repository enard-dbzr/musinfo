package com.plux.domain.model;

import java.util.Date;

public record BandMember(
        Integer id,
        Band band,
        Member member,
        String role,
        Date startDate,
        Date endDate
) {
}
