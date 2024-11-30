package com.plux.port.api.band;

import com.plux.domain.model.Album;
import com.plux.domain.model.Band;

import java.util.List;
import java.util.UUID;

public interface GetBandAlbumsPort {
    List<Album> getBandAlbums(UUID userId, Band band);
}
