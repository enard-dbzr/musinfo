package com.plux.port.api.band;

import com.plux.domain.model.Band;
import com.plux.domain.model.BandMember;

import java.util.List;
import java.util.UUID;

public interface GetBandMembersPort {
    List<BandMember> getBandMembers(UUID userID, Band band);
}
