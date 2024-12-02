package com.plux.infrastructure.adapter.postgres;

import com.plux.domain.model.Band;
import com.plux.domain.model.Member;
import com.plux.port.api.DbError;
import com.plux.port.api.band.GetAllMembersPort;
import com.plux.port.api.member.GetMemberByIdPort;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MemberRepository implements GetMemberByIdPort, GetAllMembersPort {
    private final DbConnectionFactory dbConnectionFactory;

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

    public MemberRepository(DbConnectionFactory dbConnectionFactory) {
        this.dbConnectionFactory = dbConnectionFactory;
    }

    @Override
    public Member getMemberById(UUID userId, Integer memberId) {
        try {
            var con = dbConnectionFactory.getConnection(userId);
            var st = con.prepareStatement("SELECT * FROM members WHERE id = ?");
            st.setInt(1, memberId);
            var resultSet = st.executeQuery();

            resultSet.next();
            return ConstructMember(null, resultSet);

        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }
    }

    @Override
    public List<Member> getAllMembers(UUID userId) {
        var res = new ArrayList<Member>();

        try {
            var con = dbConnectionFactory.getConnection(userId);
            var st = con.prepareStatement("SELECT * FROM members");
            var resultSet = st.executeQuery();

            while (resultSet.next()) {
                res.add(ConstructMember(null, resultSet));
            }
        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }

        return res;
    }
}
