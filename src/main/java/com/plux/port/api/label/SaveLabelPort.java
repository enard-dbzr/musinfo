package com.plux.port.api.label;

import com.plux.domain.model.Label;

import java.util.UUID;

public interface SaveLabelPort {
    Label saveLabel(UUID userId, Label label);
}
