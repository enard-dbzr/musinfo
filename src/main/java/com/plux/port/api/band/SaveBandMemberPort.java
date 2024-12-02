package com.plux.port.api.band;

import com.plux.domain.model.BandMember;

import java.util.UUID;

public interface SaveBandMemberPort {
    BandMember saveBandMember(UUID userId, BandMember bandMember);
}
