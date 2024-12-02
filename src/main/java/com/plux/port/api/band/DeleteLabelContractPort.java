package com.plux.port.api.band;

import com.plux.domain.model.LabelContract;

import java.util.UUID;

public interface DeleteLabelContractPort {
    void deleteLabelContract(UUID userId, LabelContract labelContract);
}
