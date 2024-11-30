package com.plux.infrastructure.adapter.postgres;

import com.plux.domain.model.User;
import com.plux.domain.model.UserRole;
import com.plux.port.api.DbError;
import com.plux.port.api.GetUserByIdPort;

import java.sql.*;
import java.util.UUID;

public class UserRepository implements GetUserByIdPort {
    private final DbConnectionFactory dbConnectionFactory;

    public UserRepository(DbConnectionFactory dbConnectionFactory) {
        this.dbConnectionFactory = dbConnectionFactory;
    }

    @Override
    public User getUserById(UUID userId) throws DbError {
        var roleString = getUserRole(userId);
        var userName = getUserName(userId);

        UserRole role = switch (roleString) {
            case "guest" -> UserRole.GUEST;
            case "subscriber" -> UserRole.SUBSCRIBER;
            case "manager" -> UserRole.MANAGER;
            default -> throw new IllegalStateException("Unexpected value: " + roleString);
        };

        return new User(userId, userName, role);
    }

    private String getUserRole(UUID userId) {
        try {
            var con = dbConnectionFactory.getConnection(userId);

            var statement = con.prepareStatement("SELECT oid, rolname FROM pg_roles " +
                    "WHERE rolname in ('guest', 'subscriber', 'manager') " +
                    "and pg_has_role(session_user, oid, 'member');");

            var resultSet = statement.executeQuery();

            if (!resultSet.next())
                return "guest";

            return resultSet.getString("rolname");
        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }
    }

    private String getUserName(UUID userID) throws DbError {
        var con = dbConnectionFactory.getConnection(userID);
        try {
            var statement = con.createStatement();

            var resultSet = statement.executeQuery("SELECT session_user");
            resultSet.next();

            return resultSet.getString("session_user");
        } catch (SQLException e) {
            throw new DbError(e.getMessage());
        }
    }
}
