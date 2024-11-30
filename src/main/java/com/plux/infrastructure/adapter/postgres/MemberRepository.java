package com.plux.infrastructure.adapter.postgres;

import com.plux.domain.model.Member;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberRepository {
    static Member ConstructMember(String prefix, ResultSet resultSet) throws SQLException {
        var fStr = prefix != null ? prefix + "_%s" : "%s";
        return new Member(
                resultSet.getInt(fStr.formatted("id")),
                resultSet.getString(fStr.formatted("name")),
                resultSet.getString(fStr.formatted("display_name")),
                resultSet.getDate(fStr.formatted("birthday")),
                resultSet.getString(fStr.formatted("country"))
        );
    }
}
