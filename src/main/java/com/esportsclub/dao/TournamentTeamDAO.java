package com.esportsclub.dao;

import com.esportsclub.model.TournamentTeam;
import com.esportsclub.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TournamentTeamDAO {

    private Connection connection;

    public TournamentTeamDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    public void insert(TournamentTeam tournamentTeam) {
        String sql = "INSERT INTO tournament_teams (tournament_id, team_id, registration_date) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, tournamentTeam.getTournamentId());
            ps.setInt(2, tournamentTeam.getTeamId());
            ps.setString(3, tournamentTeam.getRegistrationDate());
            ps.executeUpdate();
            System.out.println("Team added to tournament.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM tournament_teams WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Team removed from tournament. ID: " + id);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public List<TournamentTeam> getByTournamentId(int tournamentId) {
        List<TournamentTeam> list = new ArrayList<>();
        String sql = "SELECT * FROM tournament_teams WHERE tournament_id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, tournamentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return list;
    }

    public List<TournamentTeam> getByTeamId(int teamId) {
        List<TournamentTeam> list = new ArrayList<>();
        String sql = "SELECT * FROM tournament_teams WHERE team_id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return list;
    }

    public int countByTournamentId(int tournamentId) {
        String sql = "SELECT COUNT(*) as total FROM tournament_teams WHERE tournament_id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, tournamentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return 0;
    }

    public List<TournamentTeam> getAll() {
        List<TournamentTeam> list = new ArrayList<>();
        String sql = "SELECT * FROM tournament_teams";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return list;
    }

    private TournamentTeam mapResultSet(ResultSet rs) throws SQLException {
        return new TournamentTeam(
                rs.getInt("id"),
                rs.getInt("tournament_id"),
                rs.getInt("team_id"),
                rs.getString("registration_date")
        );
    }
}