package com.plux.presentation.models;

import com.plux.domain.model.Album;

public record AlbumListItem(Album album) {
    @Override
    public String toString() {
        return album.title;
    }
}
