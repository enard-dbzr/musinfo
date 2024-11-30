package com.plux.domain.model;

import java.util.Date;

public record Album(
        Integer id,
        Band band,
        Label label,
        String title,
        Date releaseDate,
        AlbumType albumType
) {
}
