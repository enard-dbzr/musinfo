package com.plux.port.api.track;

import com.plux.domain.model.Track;
import com.plux.domain.model.TrackAuthor;

import java.util.List;
import java.util.UUID;

public interface GetTrackAuthorsPort {
    List<TrackAuthor> getTrackAuthors(UUID userId, Track track);
}
