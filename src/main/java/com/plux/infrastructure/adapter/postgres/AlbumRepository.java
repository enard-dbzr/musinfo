package com.plux.infrastructure.adapter.postgres;

import com.plux.domain.model.*;
import com.plux.port.api.DbError;
import com.plux.port.api.band.GetBandAlbumsPort;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AlbumRepository implements GetBandAlbumsPort {
    private final DbConnectionFactory dbConnectionFactory;

    public AlbumRepository(DbConnectionFactory dbConnectionFactory) {
        this.dbConnectionFactory = dbConnectionFactory;
    }

    static Album ConstructAlbum(String prefix, ResultSet resultSet, Band band, Label label) throws SQLException {
        var fStr = prefix != null ? prefix + "_%s" : "%s";

        var albumType = switch (resultSet.getString(fStr.formatted("type"))) {
            case "single" -> AlbumType.SINGLE;
            case "album" -> AlbumType.AlBUM;
            case "mini-album" -> AlbumType.MINI_ALBUM;
            default ->
                    throw new IllegalStateException("Unexpected value");
        };

        return new Album(
                resultSet.getInt(fStr.formatted("id")),
                band,
                label,
                resultSet.getString(fStr.formatted("title")),
                resultSet.getDate(fStr.formatted("release_date")),
                albumType
        );
    }

    @Override
    public List<Album> getBandAlbums(UUID userId, Band band) {
        var res = new ArrayList<Album>();

        try {
            var con = dbConnectionFactory.getConnection(userId);
            var st = con.prepareStatement("""
                SELECT
                    *
                FROM
                    albums
                WHERE
                    albums.band_id = ?;
                
                """);

            st.setInt(1, band.id());
            var resultSet = st.executeQuery();

            while (resultSet.next()) {
                res.add(ConstructAlbum(null, resultSet, band, null));
            }
        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }

        return res;
    }
}
