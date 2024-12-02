package com.plux.presentation.models;

import com.plux.domain.model.Band;
import com.plux.domain.model.Label;
import com.plux.domain.model.LabelContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ContractTableItem {
    public static SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    public static GregorianCalendar border = new GregorianCalendar(3000, Calendar.JANUARY, 1);

    private LabelContract source;

    private Label label;
    private Date start;
    private Date end = border.getTime();


    public String getLabelName() {
        return label != null ? label.name() : "";
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public String getStart() {
        return start == null ? null : df.format(start);
    }

    public void setStart(String start) throws ParseException {
        this.start = df.parse(start);
    }

    public String getEnd() {
        return end.equals(border.getTime()) ? null : df.format(end);
    }

    public void setEnd(String end) throws ParseException {
        this.end = df.parse(end);
    }

    public static ContractTableItem from(LabelContract labelContract) {
        var res = new ContractTableItem();
        res.label = labelContract.label;
        res.start = labelContract.startDate;
        res.end = labelContract.endDate;

        res.source = labelContract;

        return res;
    }

    public LabelContract constructModel(Band band) {
        if (source == null) {
            source = new LabelContract(null, band, label);
        }

        source.startDate = start;
        source.endDate = end;

        return source;
    }

}
