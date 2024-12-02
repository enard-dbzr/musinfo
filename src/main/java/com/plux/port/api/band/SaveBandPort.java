package com.plux.port.api.band;

import com.plux.domain.model.Band;

import java.util.UUID;

public interface SaveBandPort {
    Band saveBand(UUID userId, Band band);
}
