package com.plux.port.api.band;

import com.plux.domain.model.Label;

import java.util.List;
import java.util.UUID;

public interface GetAllLabelsPort {
    List<Label> getAllLabels(UUID userId);
}
