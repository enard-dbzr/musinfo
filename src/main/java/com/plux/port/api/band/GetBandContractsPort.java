package com.plux.port.api.band;

import com.plux.domain.model.Band;
import com.plux.domain.model.LabelContract;

import java.util.List;
import java.util.UUID;

public interface GetBandContractsPort {
    List<LabelContract> getBandContracts(UUID userId, Band band);
}
