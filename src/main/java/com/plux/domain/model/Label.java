package com.plux.domain.model;

public class Label {
    public final Integer id;
    public String name;
    public String description;
    public String address;
    public String contactInfo;

    public Label(Integer id, String name, String description, String address, String contactInfo) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.contactInfo = contactInfo;
    }

    public Label() {
        id = null;
    }

    @Override
    public String toString() {
        return "Label{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                '}';
    }
}