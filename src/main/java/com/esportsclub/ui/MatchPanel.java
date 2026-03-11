package com.esportsclub.ui;

import com.esportsclub.model.Match;
import com.esportsclub.service.MatchService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MatchPanel extends JPanel {

    private MatchService matchService;

    // Table
    private JTable matchTable;
    private DefaultTableModel tableModel;

    // Input fields
    private JTextField tournamentIdField;
    private JTextField team1IdField;
    private JTextField team2IdField;
    private JTextField matchDateField;
    private JTextField winnerIdField;
    private JTextField team1ScoreField;
    private JTextField team2ScoreField;
    private JTextField matchIdField;

    // Buttons
    private JButton addButton;
    private JButton updateResultButton;
    private JButton deleteButton;
    private JButton refreshButton;

    public MatchPanel() {
        this.matchService = new MatchService();
        setLayout(new BorderLayout());
        initComponents();
        loadMatches();
    }

    private void initComponents() {

        // Title
        JLabel title = new JLabel("Match Management", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Tournament ID", "Team 1 ID", "Team 2 ID", "Winner ID", "Score", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        matchTable = new JTable(tableModel);
        matchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(matchTable);
        scrollPane.setPreferredSize(new Dimension(750, 300));
        add(scrollPane, BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Match Details"));

        inputPanel.add(new JLabel("Match ID (for update/delete):"));
        matchIdField = new JTextField();
        inputPanel.add(matchIdField);

        inputPanel.add(new JLabel("Tournament ID:"));
        tournamentIdField = new JTextField();
        inputPanel.add(tournamentIdField);

        inputPanel.add(new JLabel("Team 1 ID:"));
        team1IdField = new JTextField();
        inputPanel.add(team1IdField);

        inputPanel.add(new JLabel("Team 2 ID:"));
        team2IdField = new JTextField();
        inputPanel.add(team2IdField);

        inputPanel.add(new JLabel("Match Date (YYYY-MM-DD):"));
        matchDateField = new JTextField();
        inputPanel.add(matchDateField);

        inputPanel.add(new JLabel("Winner ID (for result):"));
        winnerIdField = new JTextField();
        inputPanel.add(winnerIdField);

        inputPanel.add(new JLabel("Team 1 Score:"));
        team1ScoreField = new JTextField();
        inputPanel.add(team1ScoreField);

        inputPanel.add(new JLabel("Team 2 Score:"));
        team2ScoreField = new JTextField();
        inputPanel.add(team2ScoreField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        addButton = new JButton("Add Match");
        updateResultButton = new JButton("Enter Result");
        deleteButton = new JButton("Delete Match");
        refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(updateResultButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> addMatch());
        updateResultButton.addActionListener(e -> enterResult());
        deleteButton.addActionListener(e -> deleteMatch());
        refreshButton.addActionListener(e -> loadMatches());

        // Table row click - auto fill fields
        matchTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = matchTable.getSelectedRow();
            if (selectedRow != -1) {
                matchIdField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                tournamentIdField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                team1IdField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                team2IdField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                winnerIdField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                matchDateField.setText(tableModel.getValueAt(selectedRow, 6).toString());
            }
        });
    }

    private void addMatch() {
        try {
            int tournamentId = Integer.parseInt(tournamentIdField.getText().trim());
            int team1Id = Integer.parseInt(team1IdField.getText().trim());
            int team2Id = Integer.parseInt(team2IdField.getText().trim());
            String matchDate = matchDateField.getText().trim();

            Match match = new Match(0, tournamentId, team1Id, team2Id, 0, 0, 0, matchDate, "SCHEDULED");
            boolean success = matchService.createMatch(match);

            if (success) {
                JOptionPane.showMessageDialog(this, "Match added successfully!");
                clearFields();
                loadMatches();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add match!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please fill all fields correctly!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enterResult() {
        try {
            int matchId = Integer.parseInt(matchIdField.getText().trim());
            int winnerId = Integer.parseInt(winnerIdField.getText().trim());
            int team1Score = Integer.parseInt(team1ScoreField.getText().trim());
            int team2Score = Integer.parseInt(team2ScoreField.getText().trim());

            boolean success = matchService.enterResult(matchId, winnerId, team1Score, team2Score);

            if (success) {
                JOptionPane.showMessageDialog(this, "Result entered successfully!");
                clearFields();
                loadMatches();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to enter result!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please fill all fields correctly!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMatch() {
        try {
            int matchId = Integer.parseInt(matchIdField.getText().trim());
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this match?");
            if (confirm == JOptionPane.YES_OPTION) {
                matchService.deleteMatch(matchId);
                JOptionPane.showMessageDialog(this, "Match deleted successfully!");
                clearFields();
                loadMatches();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Match ID!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMatches() {
        tableModel.setRowCount(0);
        List<Match> matches = matchService.getAllMatches();
        for (Match m : matches) {
            tableModel.addRow(new Object[]{
                    m.getId(),
                    m.getTournamentId(),
                    m.getTeam1Id(),
                    m.getTeam2Id(),
                    m.getWinnerId(),
                    m.getTeam1Score() + " - " + m.getTeam2Score(),
                    m.getMatchDate(),
                    m.getStatus()
            });
        }
    }

    private void clearFields() {
        matchIdField.setText("");
        tournamentIdField.setText("");
        team1IdField.setText("");
        team2IdField.setText("");
        matchDateField.setText("");
        winnerIdField.setText("");
        team1ScoreField.setText("");
        team2ScoreField.setText("");
    }
}