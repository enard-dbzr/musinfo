package com.plux.presentation.models;

import com.plux.domain.model.Label;

public record LabelListItem(Label label) {
    @Override
    public String toString() {
        return label.name;
    }
}
