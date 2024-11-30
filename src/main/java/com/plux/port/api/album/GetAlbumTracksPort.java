package com.plux.port.api.album;

import com.plux.domain.model.Album;
import com.plux.domain.model.Track;

import java.util.List;
import java.util.UUID;

public interface GetAlbumTracksPort {
    List<Track> getAlbumTracks(UUID userId, Album album);
}
