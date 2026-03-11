package com.esportsclub.model;

public class Team {

    private int id;
    private String name;
    private int gameId;
    private int maxCapacity;
    private String status;

    public Team() {}

    public Team(int id, String name, int gameId, int maxCapacity, String status) {
        this.id = id;
        this.name = name;
        this.gameId = gameId;
        this.maxCapacity = maxCapacity;
        this.status = status;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getGameId() { return gameId; }
    public int getMaxCapacity() { return maxCapacity; }
    public String getStatus() { return status; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setGameId(int gameId) { this.gameId = gameId; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Team{id=" + id + ", name='" + name + "', gameId=" + gameId + ", maxCapacity=" + maxCapacity + "}";
    }
}