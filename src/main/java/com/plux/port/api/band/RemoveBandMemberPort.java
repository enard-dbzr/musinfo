package com.plux.port.api.band;

import com.plux.domain.model.BandMember;

import java.util.UUID;

public interface RemoveBandMemberPort {
    void removeBandMember(UUID userId, BandMember bandMember);
}
