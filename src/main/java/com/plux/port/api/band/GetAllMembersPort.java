package com.plux.port.api.band;

import com.plux.domain.model.Member;

import java.util.List;
import java.util.UUID;

public interface GetAllMembersPort {
    List<Member> getAllMembers(UUID userId);
}
