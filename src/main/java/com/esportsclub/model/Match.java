package com.esportsclub.model;

public class Match {

    private int id;
    private int tournamentId;  // hangi turnuvaya ait
    private int team1Id;       // 1. takım
    private int team2Id;       // 2. takım
    private int winnerId;      // kazanan takım (0 ise henüz oynanmadı)
    private int team1Score;
    private int team2Score;
    private String matchDate;
    private String status;     // "SCHEDULED", "ONGOING", "FINISHED"

    public Match() {}

    public Match(int id, int tournamentId, int team1Id, int team2Id, int winnerId,
                 int team1Score, int team2Score, String matchDate, String status) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.winnerId = winnerId;
        this.team1Score = team1Score;
        this.team2Score = team2Score;
        this.matchDate = matchDate;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public int getTournamentId() { return tournamentId; }
    public int getTeam1Id() { return team1Id; }
    public int getTeam2Id() { return team2Id; }
    public int getWinnerId() { return winnerId; }
    public int getTeam1Score() { return team1Score; }
    public int getTeam2Score() { return team2Score; }
    public String getMatchDate() { return matchDate; }
    public String getStatus() { return status; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTournamentId(int tournamentId) { this.tournamentId = tournamentId; }
    public void setTeam1Id(int team1Id) { this.team1Id = team1Id; }
    public void setTeam2Id(int team2Id) { this.team2Id = team2Id; }
    public void setWinnerId(int winnerId) { this.winnerId = winnerId; }
    public void setTeam1Score(int team1Score) { this.team1Score = team1Score; }
    public void setTeam2Score(int team2Score) { this.team2Score = team2Score; }
    public void setMatchDate(String matchDate) { this.matchDate = matchDate; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Match{id=" + id + ", team1Id=" + team1Id + ", team2Id=" + team2Id + ", winnerId=" + winnerId + "}";
    }
}