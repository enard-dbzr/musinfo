package com.plux.infrastructure.adapter.postgres;

import com.plux.domain.model.Label;
import com.plux.domain.model.Member;
import com.plux.port.api.DbError;
import com.plux.port.api.label.GetLabelByIdPort;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LabelRepository implements GetLabelByIdPort {
    private final DbConnectionFactory dbConnectionFactory;

    public LabelRepository(DbConnectionFactory dbConnectionFactory) {
        this.dbConnectionFactory = dbConnectionFactory;
    }

    static Label ConstructLabel(String prefix, ResultSet resultSet) throws SQLException {
        var fStr = prefix != null ? prefix + "_%s" : "%s";
        return new Label(
                resultSet.getInt(fStr.formatted("id")),
                resultSet.getString(fStr.formatted("name")),
                resultSet.getString(fStr.formatted("description")),
                resultSet.getString(fStr.formatted("address")),
                resultSet.getString(fStr.formatted("contact_information"))
        );
    }

    @Override
    public Label getLabelById(UUID userID, Integer id) {
        try {
            var con = dbConnectionFactory.getConnection(userID);
            var st = con.prepareStatement("SELECT * FROM labels WHERE id = ?");
            st.setInt(1, id);
            var resultSet = st.executeQuery();

            resultSet.next();
            return ConstructLabel(null, resultSet);

        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }
    }
}
