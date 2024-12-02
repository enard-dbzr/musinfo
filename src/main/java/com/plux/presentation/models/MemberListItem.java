package com.plux.presentation.models;

import com.plux.domain.model.Member;

public record MemberListItem(Member member) {
    @Override
    public String toString() {
        return member.displayName();
    }
}
