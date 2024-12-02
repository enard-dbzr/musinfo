package com.plux.port.api.member;

import com.plux.domain.model.Member;

import java.util.UUID;

public interface SaveMemberPort {
    Member saveMember(UUID userId, Member member);
}
