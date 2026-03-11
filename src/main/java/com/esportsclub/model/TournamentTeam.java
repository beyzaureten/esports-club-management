package com.esportsclub.model;

public class TournamentTeam {

    private int id;
    private int tournamentId;
    private int teamId;
    private String registrationDate;

    public TournamentTeam() {}

    public TournamentTeam(int id, int tournamentId, int teamId, String registrationDate) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.teamId = teamId;
        this.registrationDate = registrationDate;
    }

    public int getId() { return id; }
    public int getTournamentId() { return tournamentId; }
    public int getTeamId() { return teamId; }
    public String getRegistrationDate() { return registrationDate; }

    public void setId(int id) { this.id = id; }
    public void setTournamentId(int tournamentId) { this.tournamentId = tournamentId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }

    @Override
    public String toString() {
        return "TournamentTeam{id=" + id + ", tournamentId=" + tournamentId + ", teamId=" + teamId + "}";
    }
}