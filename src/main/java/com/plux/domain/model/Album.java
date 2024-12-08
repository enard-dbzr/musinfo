package com.plux.domain.model;

import java.util.Date;

public class Album {
    public final Integer id;
    public final Band band;
    public Label label;
    public String title;
    public Date releaseDate;
    public AlbumType albumType;

    public Album(Integer id, Band band, Label label, String title, Date releaseDate, AlbumType albumType) {
        this.id = id;
        this.band = band;
        this.label = label;
        this.title = title;
        this.releaseDate = releaseDate;
        this.albumType = albumType;
    }

    public Album() {
        id = null;
        band = null;
    }
}
