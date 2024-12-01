package com.plux.port.api.track;

import com.plux.domain.model.Track;

import java.util.UUID;

public interface GetTrackByIdPort {
    Track getTrackById(UUID userId, Integer trackId);
}
