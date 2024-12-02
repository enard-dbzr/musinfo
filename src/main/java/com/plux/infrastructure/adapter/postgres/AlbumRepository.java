package com.plux.infrastructure.adapter.postgres;

import com.plux.domain.model.*;
import com.plux.port.api.DbError;
import com.plux.port.api.album.GetAlbumByIdPort;
import com.plux.port.api.band.GetBandAlbumsPort;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AlbumRepository implements GetBandAlbumsPort, GetAlbumByIdPort {
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

            st.setInt(1, band.id);
            var resultSet = st.executeQuery();

            while (resultSet.next()) {
                res.add(ConstructAlbum(null, resultSet, band, null));
            }
        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }

        return res;
    }

    @Override
    public Album getAlbumById(UUID userId, Integer albumId) {
        try {
            var con = dbConnectionFactory.getConnection(userId);
            var st = con.prepareStatement("""
SELECT a.id AS a_id,
       a.title AS a_title,
       a.release_date AS a_release_date,
       a.type AS a_type,
       
       b.id AS b_id,
       b.description AS b_description,
       b.name AS b_name,
       
       l.id AS l_id,
       l.name AS l_name,
       l.description AS l_description,
       l.address AS l_address,
       l.contact_information AS l_contact_information
       
FROM albums AS a
JOIN bands b ON b.id = a.band_id
LEFT JOIN labels l ON l.id = a.label_id
WHERE a.id = ?;
""");
            st.setInt(1, albumId);
            var resultSet = st.executeQuery();

            resultSet.next();

            var band = BandRepository.ConstructBand("b", resultSet);
            var label = LabelRepository.ConstructLabel("l", resultSet);
            return ConstructAlbum("a", resultSet, band, label);

        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }
    }
}
