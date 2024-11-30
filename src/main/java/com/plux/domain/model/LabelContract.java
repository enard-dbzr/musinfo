package com.plux.domain.model;

import java.util.Date;

public record LabelContract(
        Band band,
        Label label,
        Date startDate,
        Date endDate
) {
}
