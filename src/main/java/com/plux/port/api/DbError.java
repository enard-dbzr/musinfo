package com.plux.port.api;

public class DbError extends RuntimeException {
    public DbError(String message) {
        super(message);
    }
}
