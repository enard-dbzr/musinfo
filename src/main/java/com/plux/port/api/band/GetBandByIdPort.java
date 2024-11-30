package com.plux.port.api.band;

import com.plux.domain.model.Band;

import java.util.UUID;

public interface GetBandByIdPort {
    Band GetBandById(UUID userId, Integer id);
}
