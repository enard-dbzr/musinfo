package com.plux.domain.model;

public record TrackAuthor (
        Track track,
        Band band,
        TrackAuthorRole role
) {
}
