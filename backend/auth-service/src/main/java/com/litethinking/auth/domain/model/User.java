package com.litethinking.auth.domain.model;

import java.util.UUID;

public class User {

    private final UUID id;
    private final String username;
    private final String password;
    private final Role role;
    private final UUID clientId;

    public User(UUID id, String username, String password, Role role, UUID clientId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.clientId = clientId;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public UUID getClientId() {
        return clientId;
    }
}
