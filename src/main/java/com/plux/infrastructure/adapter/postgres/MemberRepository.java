package com.plux.infrastructure.adapter.postgres;

import com.plux.domain.model.Member;
import com.plux.port.api.DbError;
import com.plux.port.api.band.GetAllMembersPort;
import com.plux.port.api.member.DeleteMemberPort;
import com.plux.port.api.member.GetMemberByIdPort;
import com.plux.port.api.member.SaveMemberPort;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MemberRepository implements GetMemberByIdPort, GetAllMembersPort, SaveMemberPort, DeleteMemberPort {
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

    @Override
    public Member saveMember(UUID userId, Member member) {
        try {
            var con = dbConnectionFactory.getConnection(userId);
            if (member.id == null) {
                var st = con.prepareStatement("""
INSERT INTO members (name, display_name, birthday, country) VALUES (?, ?, ?, ?) RETURNING id
""");

                st.setString(1, member.name);
                st.setString(2, member.displayName);
                st.setDate(3, member.birthday == null ? null : new Date(member.birthday.getTime()));
                st.setString(4, member.countryCode);

                var resultSet = st.executeQuery();

                resultSet.next();

                return new Member(resultSet.getInt("id"), member.name, member.displayName,
                        member.birthday, member.countryCode);
            } else {
                var st = con.prepareStatement("""
UPDATE members SET name = ?, display_name = ?, birthday = ?, country = ? WHERE id = ?""");

                st.setString(1, member.name);
                st.setString(2, member.displayName);
                st.setDate(3, member.birthday == null ? null : new Date(member.birthday.getTime()));
                st.setString(4, member.countryCode);
                st.setInt(5, member.id);

                st.executeUpdate();

                return member;
            }

        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }
    }

    @Override
    public void deleteMember(UUID userId, Member member) {
        if (member.id == null) return;

        try {
            var con = dbConnectionFactory.getConnection(userId);

            var st = con.prepareStatement("DELETE FROM members WHERE id = ?");

            st.setInt(1, member.id);

            st.executeUpdate();
        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }
    }
}
