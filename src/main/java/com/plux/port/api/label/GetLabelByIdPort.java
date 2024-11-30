package com.plux.port.api.label;

import com.plux.domain.model.Label;

import java.util.UUID;

public interface GetLabelByIdPort {
    Label getLabelById(UUID userID, Integer id);
}
