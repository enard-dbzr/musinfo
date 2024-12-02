package com.plux.port.api.band;

import com.plux.domain.model.LabelContract;

import java.util.UUID;

public interface SaveLabelContractPort {
    LabelContract saveLabelContract(UUID userId, LabelContract labelContract);
}
