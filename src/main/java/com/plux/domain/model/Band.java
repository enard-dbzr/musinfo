package com.plux.domain.model;

public class Band {
    public final Integer id;
    public String name;
    public String description;


    @Override
    public String toString() {
        return "Band{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public Band(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Band() {
        id = null;
    }

}