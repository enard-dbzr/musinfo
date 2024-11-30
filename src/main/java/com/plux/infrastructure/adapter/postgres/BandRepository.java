package com.plux.infrastructure.adapter.postgres;

import com.plux.domain.model.Band;
import com.plux.port.api.band.GetBandByIdPort;
import com.plux.port.api.SearchBandsPort;
import com.plux.port.api.DbError;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BandRepository implements SearchBandsPort, GetBandByIdPort {
    private final DbConnectionFactory dbConnectionFactory;

    public BandRepository(DbConnectionFactory dbConnectionFactory) {
        this.dbConnectionFactory = dbConnectionFactory;
    }

    static Band ConstructBand(ResultSet resultSet) throws SQLException {
        return new Band(resultSet.getInt("id"),
                resultSet.getString("name"), resultSet.getString("description"));
    }

    @Override
    public List<Band> Search(UUID userId, String s) {
        var res = new ArrayList<Band>();

        try {
            var con = dbConnectionFactory.getConnection(userId);
            var st = con.prepareStatement("SELECT * FROM bands WHERE name ILIKE ?");
            st.setString(1, "%" + s + "%");
            var resultSet = st.executeQuery();

            while (resultSet.next()) {
                res.add(ConstructBand(resultSet));
            }
        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }

        return res;
    }

    @Override
    public Band GetBandById(UUID userId, Integer id) {
        try {
            var con = dbConnectionFactory.getConnection(userId);
            var st = con.prepareStatement("SELECT * FROM bands WHERE id = ?");
            st.setInt(1, id);
            var resultSet = st.executeQuery();

            resultSet.next();
            return ConstructBand(resultSet);

        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }
    }
}
