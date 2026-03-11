package com.esportsclub.dao;

import com.esportsclub.model.TeamMember;
import com.esportsclub.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamMemberDAO {

    private Connection connection;

    public TeamMemberDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    // Add new team member
    public void insert(TeamMember member) {
        String sql = "INSERT INTO team_members (team_id, user_id, join_date) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, member.getTeamId());
            ps.setInt(2, member.getUserId());
            ps.setString(3, member.getJoinDate());
            ps.executeUpdate();
            System.out.println("Member added to team.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Remove team member
    public void delete(int id) {
        String sql = "DELETE FROM team_members WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Member removed. ID: " + id);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Get all members of a team
    public List<TeamMember> getByTeamId(int teamId) {
        List<TeamMember> members = new ArrayList<>();
        String sql = "SELECT * FROM team_members WHERE team_id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                members.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return members;
    }

    // Get all teams a user belongs to
    public List<TeamMember> getByUserId(int userId) {
        List<TeamMember> members = new ArrayList<>();
        String sql = "SELECT * FROM team_members WHERE user_id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                members.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return members;
    }

    // Count members in a team
    public int countByTeamId(int teamId) {
        String sql = "SELECT COUNT(*) as total FROM team_members WHERE team_id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return 0;
    }

    // Get all members
    public List<TeamMember> getAll() {
        List<TeamMember> members = new ArrayList<>();
        String sql = "SELECT * FROM team_members";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                members.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return members;
    }

    private TeamMember mapResultSet(ResultSet rs) throws SQLException {
        return new TeamMember(
                rs.getInt("id"),
                rs.getInt("team_id"),
                rs.getInt("user_id"),
                rs.getString("join_date")
        );
    }
}