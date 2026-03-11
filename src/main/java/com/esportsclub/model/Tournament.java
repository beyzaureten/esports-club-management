package com.esportsclub.model;

public class Tournament {

    private int id;
    private String name;
    private int gameId;
    private int maxTeams;
    private String startDate;
    private String endDate;
    private String status;

    public Tournament() {}

    public Tournament(int id, String name, int gameId, int maxTeams, String startDate, String endDate, String status) {
        this.id = id;
        this.name = name;
        this.gameId = gameId;
        this.maxTeams = maxTeams;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getGameId() { return gameId; }
    public int getMaxTeams() { return maxTeams; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getStatus() { return status; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setGameId(int gameId) { this.gameId = gameId; }
    public void setMaxTeams(int maxTeams) { this.maxTeams = maxTeams; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Tournament{id=" + id + ", name='" + name + "', status='" + status + "'}";
    }
}