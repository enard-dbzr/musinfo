package com.plux.infrastructure.adapter.postgres;

import com.plux.domain.model.*;
import com.plux.port.api.DbError;
import com.plux.port.api.track.GetTrackAuthorsPort;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TrackAuthorsRepository implements GetTrackAuthorsPort {
    private final DbConnectionFactory dbConnectionFactory;

    public TrackAuthorsRepository(DbConnectionFactory dbConnectionFactory) {
        this.dbConnectionFactory = dbConnectionFactory;
    }

    static TrackAuthor ConstructTrackAuthor(String prefix, ResultSet resultSet, Track track, Band band) throws SQLException {
        var fStr = prefix != null ? prefix + "_%s" : "%s";

        var role = resultSet.getString(fStr.formatted("role"));
        var authorRole = switch (role) {
            case "main" -> TrackAuthorRole.MAIN;
            case "feat" -> TrackAuthorRole.FEAT;
            case "remix" -> TrackAuthorRole.REMIX;
            default -> throw new IllegalStateException("Unexpected value: " + role);
        };

        return new TrackAuthor(track, band, authorRole);
    }

    @Override
    public List<TrackAuthor> getTrackAuthors(UUID userId, Track track) {
        var res = new ArrayList<TrackAuthor>();

        try {
            var con = dbConnectionFactory.getConnection(userId);
            var st = con.prepareStatement("""
SELECT ta.role AS ta_role,
       b.id AS b_id,
       b.name AS b_name,
       b.description AS b_description
FROM trackauthors AS ta
JOIN bands b on b.id = ta.band_id
WHERE ta.track_id = ?
""");

            st.setInt(1, track.id());
            var resultSet = st.executeQuery();

            while (resultSet.next()) {
                var band = BandRepository.ConstructBand("b", resultSet);
                res.add(ConstructTrackAuthor("ta", resultSet, track, band));
            }
        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }

        return res;
    }
}
