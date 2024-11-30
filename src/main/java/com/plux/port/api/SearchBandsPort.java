package com.plux.port.api;

import com.plux.domain.model.Band;

import java.util.List;
import java.util.UUID;

public interface SearchBandsPort {
    List<Band> Search(UUID userId, String s);
}
