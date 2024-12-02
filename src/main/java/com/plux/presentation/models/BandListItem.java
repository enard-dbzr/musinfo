package com.plux.presentation.models;

import com.plux.domain.model.Band;

public record BandListItem(Band band) {
    @Override
    public String toString() {
        return band.name;
    }
}
