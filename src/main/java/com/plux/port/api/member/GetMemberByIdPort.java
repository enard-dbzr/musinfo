package com.plux.port.api.member;

import com.plux.domain.model.Member;

import java.util.UUID;

public interface GetMemberByIdPort {
    Member getMemberById(UUID userId, Integer memberId);
}
