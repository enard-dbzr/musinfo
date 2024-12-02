package com.plux.infrastructure.adapter.postgres;

import com.plux.domain.model.*;
import com.plux.port.api.DbError;
import com.plux.port.api.band.DeleteLabelContractPort;
import com.plux.port.api.band.GetBandContractsPort;
import com.plux.port.api.band.SaveLabelContractPort;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LabelContractsRepository implements GetBandContractsPort, SaveLabelContractPort, DeleteLabelContractPort {
    private final DbConnectionFactory dbConnectionFactory;

    public LabelContractsRepository(DbConnectionFactory dbConnectionFactory) {
        this.dbConnectionFactory = dbConnectionFactory;
    }

    static LabelContract ConstructLabelContract(String prefix, ResultSet resultSet, Band band, Label label) throws SQLException {
        var fStr = prefix != null ? prefix + "_%s" : "%s";
        return new LabelContract(
                resultSet.getInt(fStr.formatted("id")),
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

            st.setInt(1, band.id);
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

    @Override
    public LabelContract saveLabelContract(UUID userId, LabelContract labelContract) {
        boolean create = labelContract.id == null;

        try {
            var con = dbConnectionFactory.getConnection(userId);
            if (create) {
                var st = con.prepareStatement("""
INSERT INTO labelcontracts (band_id, label_id, start_date, end_date) VALUES (?, ?, ?, ?) RETURNING id
""");

                st.setInt(1, labelContract.band.id);
                st.setInt(2, labelContract.label.id());
                st.setDate(3, new Date(labelContract.startDate.getTime()));
                st.setDate(4, new Date(labelContract.endDate.getTime()));


                var resultSet = st.executeQuery();

                resultSet.next();

                return new LabelContract(resultSet.getInt("id"), labelContract.band, labelContract.label,
                        labelContract.startDate, labelContract.endDate);
            } else {
                var st = con.prepareStatement("""
UPDATE labelcontracts SET band_id = ?, label_id = ?, start_date = ?, end_date = ? WHERE id = ?""");

                st.setInt(1, labelContract.band.id);
                st.setInt(2, labelContract.label.id());
                st.setDate(3, new Date(labelContract.startDate.getTime()));
                st.setDate(4, new Date(labelContract.endDate.getTime()));
                st.setInt(5, labelContract.id);

                st.executeUpdate();

                return labelContract;
            }

        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }
    }

    @Override
    public void deleteLabelContract(UUID userId, LabelContract labelContract) {
        try {
            var con = dbConnectionFactory.getConnection(userId);

            var st = con.prepareStatement("DELETE FROM labelcontracts WHERE id = ?");

            st.setInt(1, labelContract.id);

            st.executeUpdate();
        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }
    }
}
