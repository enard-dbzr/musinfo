package com.plux.port.api;

import java.util.UUID;

public interface AuthPort {
    UUID authenticate(String login, String password) throws DbError;
}
