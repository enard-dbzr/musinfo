package com.plux.infrastructure.adapter.postgres;

import com.plux.domain.model.Label;
import com.plux.domain.model.Member;
import com.plux.port.api.DbError;
import com.plux.port.api.band.GetAllLabelsPort;
import com.plux.port.api.label.GetLabelByIdPort;
import com.plux.port.api.label.SaveLabelPort;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LabelRepository implements GetLabelByIdPort, GetAllLabelsPort, SaveLabelPort {
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

    @Override
    public List<Label> getAllLabels(UUID userId) {
        var res = new ArrayList<Label>();

        try {
            var con = dbConnectionFactory.getConnection(userId);
            var st = con.prepareStatement("SELECT * FROM labels");
            var resultSet = st.executeQuery();

            while (resultSet.next()) {
                res.add(ConstructLabel(null, resultSet));
            }
        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }

        return res;
    }

    @Override
    public Label saveLabel(UUID userId, Label label) {
        try {
            var con = dbConnectionFactory.getConnection(userId);
            if (label.id == null) {
                var st = con.prepareStatement("""
INSERT INTO labels (name, description, address, contact_information) VALUES (?, ?, ?, ?) RETURNING id
""");

                st.setString(1, label.name);
                st.setString(2, label.description);
                st.setString(3, label.address);
                st.setString(4, label.contactInfo);

                var resultSet = st.executeQuery();

                resultSet.next();

                return new Label(resultSet.getInt("id"), label.name, label.description,
                        label.address, label.contactInfo);
            } else {
                var st = con.prepareStatement("""
UPDATE labels SET name = ?, description = ?, address = ?, contact_information = ? WHERE id = ?""");

                st.setString(1, label.name);
                st.setString(2, label.description);
                st.setString(3, label.address);
                st.setString(4, label.contactInfo);
                st.setInt(5, label.id);

                st.executeUpdate();

                return label;
            }

        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }
    }
}
