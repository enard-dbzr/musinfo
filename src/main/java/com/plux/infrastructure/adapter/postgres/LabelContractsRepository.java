package com.plux.infrastructure.adapter.postgres;

import com.plux.domain.model.*;
import com.plux.port.api.DbError;
import com.plux.port.api.band.GetBandContractsPort;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LabelContractsRepository implements GetBandContractsPort {
    private final DbConnectionFactory dbConnectionFactory;

    public LabelContractsRepository(DbConnectionFactory dbConnectionFactory) {
        this.dbConnectionFactory = dbConnectionFactory;
    }

    static LabelContract ConstructLabelContract(String prefix, ResultSet resultSet, Band band, Label label) throws SQLException {
        var fStr = prefix != null ? prefix + "_%s" : "%s";
        return new LabelContract(
                band,
                label,
                resultSet.getDate(fStr.formatted("start_date")),
                resultSet.getDate(fStr.formatted("end_date"))
        );
    }

    @Override
    public List<LabelContract> getBandContracts(UUID userId, Band band) {
        var res = new ArrayList<LabelContract>();

        try {
            var con = dbConnectionFactory.getConnection(userId);
            var st = con.prepareStatement("""
                SELECT
                    labelcontracts.id AS lc_id,
                    labelcontracts.start_date AS lc_start_date,
                    labelcontracts.end_date AS lc_end_date,
                    l.id AS l_id,
                    l.name AS l_name,
                    l.description AS l_description,
                    l.address AS l_address,
                    l.contact_information AS l_contact_information
                FROM
                    labelcontracts
                JOIN
                    labels AS l ON l.id = labelcontracts.label_id
                WHERE
                    labelcontracts.band_id = ?;
                """);

            st.setInt(1, band.id());
            var resultSet = st.executeQuery();

            while (resultSet.next()) {
                var label = LabelRepository.ConstructLabel("l", resultSet);
                res.add(ConstructLabelContract("lc", resultSet, band, label));
            }
        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }

        return res;
    }
}
