package com.plux.port.api;

import com.plux.domain.model.User;

import java.util.UUID;

public interface GetUserByIdPort {
    User getUserById(UUID userId) throws DbError;
}
