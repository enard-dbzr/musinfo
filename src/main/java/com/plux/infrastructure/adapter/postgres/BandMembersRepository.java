package com.plux.infrastructure.adapter.postgres;

import com.plux.domain.model.Band;
import com.plux.domain.model.BandMember;
import com.plux.domain.model.Member;
import com.plux.port.api.DbError;
import com.plux.port.api.band.GetBandMembersPort;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BandMembersRepository implements GetBandMembersPort {
    private final DbConnectionFactory dbConnectionFactory;

    public BandMembersRepository(DbConnectionFactory dbConnectionFactory) {
        this.dbConnectionFactory = dbConnectionFactory;
    }

    static BandMember ConstructBandMember(String prefix, ResultSet resultSet, Band band, Member member) throws SQLException {
        var fStr = prefix != null ? prefix + "_%s" : "%s";
        return new BandMember(
                resultSet.getInt(fStr.formatted("id")),
                band,
                member,
                resultSet.getString(fStr.formatted("role")),
                resultSet.getDate(fStr.formatted("start_date")),
                resultSet.getDate(fStr.formatted("end_date"))
        );
    }

    @Override
    public List<BandMember> getBandMembers(UUID userID, Band band) {
        var res = new ArrayList<BandMember>();

        try {
            var con = dbConnectionFactory.getConnection(userID);
            var st = con.prepareStatement("""
                SELECT
                    bandmembers.id AS bm_id,
                    bandmembers.role AS bm_role,
                    bandmembers.start_date AS bm_start_date,
                    bandmembers.end_date AS bm_end_date,
                    m.id AS m_id,
                    m.name AS m_name,
                    m.display_name AS m_display_name,
                    m.birthday AS m_birthday,
                    m.country AS m_country
                FROM
                    bandmembers
                JOIN
                    members AS m ON m.id = bandmembers.member_id
                WHERE
                    bandmembers.band_id = ?;
                
                """);

            st.setInt(1, band.id());
            var resultSet = st.executeQuery();

            while (resultSet.next()) {
                var member = MemberRepository.ConstructMember("m", resultSet);
                res.add(ConstructBandMember("bm", resultSet, band, member));
            }
        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }

        return res;
    }
}
