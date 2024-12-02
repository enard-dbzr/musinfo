package com.plux.port.api.member;

import com.plux.domain.model.Member;

import java.util.UUID;

public interface DeleteMemberPort {
    void deleteMember(UUID userId, Member member);
}
