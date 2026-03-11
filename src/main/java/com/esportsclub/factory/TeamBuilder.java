package com.esportsclub.factory;

import com.esportsclub.model.Team;

public class TeamBuilder {

    private int id;
    private String name;
    private int gameId;
    private int maxCapacity;
    private String status;

    // Builder Pattern - build a Team object step by step
    public TeamBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public TeamBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public TeamBuilder setGameId(int gameId) {
        this.gameId = gameId;
        return this;
    }

    public TeamBuilder setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        return this;
    }

    public TeamBuilder setStatus(String status) {
        this.status = status;
        return this;
    }

    // Final step - build and return the Team object
    public Team build() {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Error: Team name cannot be empty!");
            return null;
        }
        if (maxCapacity <= 0) {
            maxCapacity = 5; // default capacity
        }
        if (status == null) {
            status = "ACTIVE"; // default status
        }
        return new Team(id, name, gameId, maxCapacity, status);
    }
}