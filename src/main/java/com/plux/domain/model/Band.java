package com.plux.domain.model;

import java.util.Objects;

public class Band {
    private final Integer id;
    private String name;
    private String description;


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Band band)) return false;

        return Objects.equals(id, band.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

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

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}