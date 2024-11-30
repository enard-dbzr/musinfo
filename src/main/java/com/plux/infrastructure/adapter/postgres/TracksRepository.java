package com.plux.infrastructure.adapter.postgres;

import com.plux.domain.model.*;
import com.plux.port.api.DbError;
import com.plux.port.api.album.GetAlbumTracksPort;
import org.postgresql.util.PGInterval;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.*;

public class TracksRepository implements GetAlbumTracksPort {
    private final DbConnectionFactory dbConnectionFactory;

    public TracksRepository(DbConnectionFactory dbConnectionFactory) {
        this.dbConnectionFactory = dbConnectionFactory;
    }

    static Track ConstructTrack(String prefix, ResultSet resultSet) throws SQLException {
        var fStr = prefix != null ? prefix + "_%s" : "%s";

        var duration = (PGInterval)resultSet.getObject(fStr.formatted("duration"));

        var e = new GregorianCalendar();
        e.setTimeInMillis(0);

        duration.add(e);

        return new Track(
                resultSet.getInt(fStr.formatted("id")),
                resultSet.getString(fStr.formatted("title")),
                Duration.ofSeconds(e.getTimeInMillis() / 1000)
        );
    }

    @Override
    public List<Track> getAlbumTracks(UUID userId, Album album) {
        var res = new ArrayList<Track>();

        try {
            var con = dbConnectionFactory.getConnection(userId);
            var st = con.prepareStatement("""
SELECT t.id AS t_id,
       t.title AS t_title,
       t.duration AS t_duration
FROM trackinalbums AS tia
JOIN tracks t ON t.id = tia.track_id
WHERE tia.album_id = ?
""");

            st.setInt(1, album.id());
            var resultSet = st.executeQuery();

            while (resultSet.next()) {
                res.add(ConstructTrack("t", resultSet));
            }
        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }
        return res;
    }
}
