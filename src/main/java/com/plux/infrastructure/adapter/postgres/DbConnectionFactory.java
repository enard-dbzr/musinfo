package com.plux.infrastructure.adapter.postgres;

import com.plux.port.api.AuthPort;
import com.plux.port.api.DbError;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class DbConnectionFactory implements AuthPort {
    Map<UUID, Connection> connections = new HashMap<>();

    private String host;
    private int port;
    private String dbName;

    public DbConnectionFactory(String host, int port, String dbName) {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
    }

    Connection getConnection(UUID userId) {
        return connections.get(userId);
    }

    @Override
    public UUID authenticate(String login, String password) throws DbError {
        String uri = String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s", host, port, dbName, login, password);
        var id = UUID.randomUUID();

        try {
            var con = DriverManager.getConnection(uri);
            connections.put(id, con);
        } catch (SQLException e) {
            throw new DbError("Connection failed: " + e.getMessage());
        }

        return id;
    }
}
