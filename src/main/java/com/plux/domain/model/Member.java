package com.plux.domain.model;

import java.util.Date;

//public record Member(
//        Integer id,
//        String name,
//        String displayName,
//        Date birthday,
//        String countryCode
//) {}

public class Member {
    public final Integer id;
    public String name;
    public String displayName;
    public Date birthday;
    public String countryCode;

    public Member(Integer id, String name, String displayName, Date birthday, String countryCode) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.birthday = birthday;
        this.countryCode = countryCode;
    }

    public Member() {
        id = null;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", birthday=" + birthday +
                ", countryCode='" + countryCode + '\'' +
                '}';
    }
}
