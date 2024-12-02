package com.plux.domain.model;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

public class BandMember {
    private final Integer id;
    private Band band;
    private Member member;
    private String role;
    private Date startDate;
    private Date endDate;

    public BandMember(Integer id, Band band, Member member, String role, Date startDate, Date endDate) {
        this.id = id;
        this.band = band;
        this.member = member;
        this.role = role;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public BandMember() {
        this.id = null;
        startDate = new Date();
        endDate = (new GregorianCalendar(3000, Calendar.JANUARY, 1)).getTime();
    }

    public Integer getId() {
        return id;
    }

    public Band getBand() {
        return band;
    }

    public void setBand(Band band) {
        this.band = band;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "BandMember{" +
                "id=" + id +
                ", band=" + band +
                ", member=" + member +
                ", role='" + role + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}

