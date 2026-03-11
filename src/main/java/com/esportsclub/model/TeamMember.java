package com.esportsclub.model;

public class TeamMember {

    private int id;
    private int teamId;
    private int userId;
    private String joinDate;

    public TeamMember() {}

    public TeamMember(int id, int teamId, int userId, String joinDate) {
        this.id = id;
        this.teamId = teamId;
        this.userId = userId;
        this.joinDate = joinDate;
    }

    public int getId() { return id; }
    public int getTeamId() { return teamId; }
    public int getUserId() { return userId; }
    public String getJoinDate() { return joinDate; }

    public void setId(int id) { this.id = id; }
    public void setTeamId(int teamId) { this.teamId = teamId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setJoinDate(String joinDate) { this.joinDate = joinDate; }

    @Override
    public String toString() {
        return "TeamMember{id=" + id + ", teamId=" + teamId + ", userId=" + userId + "}";
    }
}