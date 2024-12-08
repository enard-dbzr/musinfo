package com.plux.port.api.album;

import com.plux.domain.model.Album;

import java.util.UUID;

public interface SaveAlbumPort {
    Album saveAlbum(UUID userId, Album album);
}
