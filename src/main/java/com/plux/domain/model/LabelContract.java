package com.plux.domain.model;

import java.util.Date;

//public record LabelContract(
//        Integer id,
//        Band band,
//        Label label,
//        Date startDate,
//        Date endDate
//) {
//}

public class LabelContract {
    public final Integer id;
    public final Band band;
    public final Label label;
    public Date startDate;
    public Date endDate;

    public LabelContract(Integer id, Band band, Label label) {
        this.id = id;
        this.band = band;
        this.label = label;
    }

    public LabelContract(Integer id, Band band, Label label, Date startDate, Date endDate) {
        this.id = id;
        this.band = band;
        this.label = label;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}