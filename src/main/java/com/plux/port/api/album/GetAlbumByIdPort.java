package com.plux.port.api.album;

import com.plux.domain.model.Album;

import java.util.UUID;

public interface GetAlbumByIdPort {
    Album getAlbumById(UUID userId, Integer albumId);
}
