package com.esportsclub.ui;

import com.esportsclub.model.Match;
import com.esportsclub.service.MatchService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MatchPanel extends JPanel {

    private static final Color BG_MAIN    = new Color(248, 247, 255);
    private static final Color BG_PANEL   = new Color(255, 255, 255);
    private static final Color BG_TABLE   = new Color(255, 255, 255);
    private static final Color BG_FORM    = new Color(250, 249, 255);
    private static final Color ACCENT     = new Color(245, 158, 11);
    private static final Color GREEN      = new Color(16, 185, 129);
    private static final Color RED        = new Color(220, 38, 38);
    private static final Color BLUE       = new Color(59, 130, 246);
    private static final Color YELLOW     = new Color(245, 158, 11);
    private static final Color ORANGE     = new Color(249, 115, 22);
    private static final Color TEXT_MAIN  = new Color(30, 27, 46);
    private static final Color TEXT_DIM   = new Color(120, 110, 150);
    private static final Color ROW_ODD    = new Color(255, 253, 248);
    private static final Color ROW_EVEN   = new Color(255, 255, 255);
    private static final Color ROW_SELECT = new Color(245, 158, 11, 15);
    private static final Color BORDER_DIM = new Color(220, 215, 240);
    private static final Color INPUT_BG   = new Color(250, 249, 255);

    private final MatchService matchService = new MatchService();

    private JTable            matchTable;
    private DefaultTableModel tableModel;
    private JPanel            formPanel;

    private JTextField tournamentIdField, team1IdField, team2IdField;
    private JTextField matchDateField, winnerIdField;
    private JTextField team1ScoreField, team2ScoreField, matchIdField;

    private JLabel lblCount;

    public MatchPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_MAIN);
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
        formPanel = buildForm();
        add(formPanel,      BorderLayout.SOUTH);
        loadMatches();
    }

    public void setAdminMode(boolean isAdmin) {
        formPanel.setVisible(isAdmin);
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(BG_PANEL);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_DIM),
                new EmptyBorder(12, 16, 12, 16)));

        JLabel title = new JLabel("Matches");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(ACCENT);
        bar.add(title, BorderLayout.WEST);

        JButton btnRefresh = solidBtn("Refresh", BLUE, 90);
        btnRefresh.addActionListener(e -> loadMatches());
        bar.add(btnRefresh, BorderLayout.EAST);
        return bar;
    }

    private JScrollPane buildTable() {
        String[] columns = {"ID", "Tournament ID", "Team 1 ID", "Team 2 ID", "Winner ID", "Score", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        matchTable = new JTable(tableModel) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) c.setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                return c;
            }
        };

        matchTable.setFont(new Font("Arial", Font.PLAIN, 13));
        matchTable.setRowHeight(32);
        matchTable.setBackground(BG_TABLE);
        matchTable.setForeground(TEXT_MAIN);
        matchTable.setGridColor(BORDER_DIM);
        matchTable.setShowHorizontalLines(true);
        matchTable.setShowVerticalLines(false);
        matchTable.setSelectionBackground(ROW_SELECT);
        matchTable.setSelectionForeground(TEXT_MAIN);
        matchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        matchTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        matchTable.getTableHeader().setBackground(new Color(255, 252, 235));
        matchTable.getTableHeader().setForeground(ACCENT);
        matchTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_DIM));
        matchTable.getTableHeader().setReorderingAllowed(false);

        matchTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(CENTER);
                setBackground(sel ? ROW_SELECT : (r % 2 == 0 ? ROW_EVEN : ROW_ODD));
                if ("FINISHED".equals(v))       setForeground(GREEN);
                else if ("ONGOING".equals(v))   setForeground(YELLOW);
                else if ("SCHEDULED".equals(v)) setForeground(BLUE);
                setFont(getFont().deriveFont(Font.BOLD));
                return this;
            }
        });

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

        JScrollPane sp = new JScrollPane(matchTable);
        sp.setBackground(BG_TABLE);
        sp.getViewport().setBackground(BG_TABLE);
        sp.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER_DIM));
        return sp;
    }

    private JPanel buildForm() {
        JPanel wrap = new JPanel(new BorderLayout(0, 0));
        wrap.setBackground(BG_FORM);
        wrap.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, ACCENT));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(10, 14, 6, 14));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 6, 4, 6);

        g.gridy = 0;
        g.gridx = 0; g.weightx = 0.08; inputPanel.add(fLbl("Match ID"), g);
        g.gridx = 1; g.weightx = 0.15; matchIdField = lightField(); inputPanel.add(matchIdField, g);
        g.gridx = 2; g.weightx = 0.08; inputPanel.add(fLbl("Tournament ID"), g);
        g.gridx = 3; g.weightx = 0.15; tournamentIdField = lightField(); inputPanel.add(tournamentIdField, g);
        g.gridx = 4; g.weightx = 0.08; inputPanel.add(fLbl("Team 1 ID"), g);
        g.gridx = 5; g.weightx = 0.15; team1IdField = lightField(); inputPanel.add(team1IdField, g);
        g.gridx = 6; g.weightx = 0.08; inputPanel.add(fLbl("Team 2 ID"), g);
        g.gridx = 7; g.weightx = 0.15; team2IdField = lightField(); inputPanel.add(team2IdField, g);

        g.gridy = 1;
        g.gridx = 0; g.weightx = 0.08; inputPanel.add(fLbl("Match Date"), g);
        g.gridx = 1; g.weightx = 0.15; matchDateField = lightField(); inputPanel.add(matchDateField, g);
        g.gridx = 2; g.weightx = 0.08; inputPanel.add(fLbl("Winner ID"), g);
        g.gridx = 3; g.weightx = 0.15; winnerIdField = lightField(); inputPanel.add(winnerIdField, g);
        g.gridx = 4; g.weightx = 0.08; inputPanel.add(fLbl("Team 1 Score"), g);
        g.gridx = 5; g.weightx = 0.15; team1ScoreField = lightField(); inputPanel.add(team1ScoreField, g);
        g.gridx = 6; g.weightx = 0.08; inputPanel.add(fLbl("Team 2 Score"), g);
        g.gridx = 7; g.weightx = 0.15; team2ScoreField = lightField(); inputPanel.add(team2ScoreField, g);

        wrap.add(inputPanel, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new BorderLayout());
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(0, 14, 10, 14));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JButton btnAdd    = solidBtn("+ Add Match",  GREEN,                    110);
        JButton btnResult = solidBtn("Enter Result", ORANGE,                   110);
        JButton btnDelete = solidBtn("Delete",       RED,                       90);
        JButton btnClear  = solidBtn("Clear",        new Color(150, 140, 180),  80);

        btnAdd.addActionListener(e    -> addMatch());
        btnResult.addActionListener(e -> enterResult());
        btnDelete.addActionListener(e -> deleteMatch());
        btnClear.addActionListener(e  -> clearFields());

        left.add(btnAdd); left.add(btnResult); left.add(btnDelete); left.add(btnClear);

        lblCount = new JLabel();
        lblCount.setFont(new Font("Arial", Font.ITALIC, 11));
        lblCount.setForeground(TEXT_DIM);

        btnRow.add(left, BorderLayout.WEST);
        btnRow.add(lblCount, BorderLayout.EAST);
        wrap.add(btnRow, BorderLayout.SOUTH);
        return wrap;
    }

    private void addMatch() {
        try {
            int tournamentId = Integer.parseInt(tournamentIdField.getText().trim());
            int team1Id      = Integer.parseInt(team1IdField.getText().trim());
            int team2Id      = Integer.parseInt(team2IdField.getText().trim());
            String matchDate = matchDateField.getText().trim();
            Match match = new Match(0, tournamentId, team1Id, team2Id, 0, 0, 0, matchDate, "SCHEDULED");
            if (matchService.createMatch(match)) {
                JOptionPane.showMessageDialog(this, "Match added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields(); loadMatches();
            } else { JOptionPane.showMessageDialog(this, "Failed to add match!", "Error", JOptionPane.ERROR_MESSAGE); }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please fill all fields correctly!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enterResult() {
        try {
            int matchId    = Integer.parseInt(matchIdField.getText().trim());
            int winnerId   = Integer.parseInt(winnerIdField.getText().trim());
            int team1Score = Integer.parseInt(team1ScoreField.getText().trim());
            int team2Score = Integer.parseInt(team2ScoreField.getText().trim());
            if (matchService.enterResult(matchId, winnerId, team1Score, team2Score)) {
                JOptionPane.showMessageDialog(this, "Result entered successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields(); loadMatches();
            } else { JOptionPane.showMessageDialog(this, "Failed to enter result!", "Error", JOptionPane.ERROR_MESSAGE); }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please fill all fields correctly!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMatch() {
        try {
            int matchId = Integer.parseInt(matchIdField.getText().trim());
            boolean confirmed = ConfirmDialog.showDeleteConfirm(this,
                    "Delete Match",
                    "Are you sure you want to delete this match?<br>This action cannot be undone.");
            if (confirmed) {
                matchService.deleteMatch(matchId);
                JOptionPane.showMessageDialog(this, "Match deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields(); loadMatches();
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
                    m.getId(), m.getTournamentId(), m.getTeam1Id(), m.getTeam2Id(),
                    m.getWinnerId(), m.getTeam1Score() + " - " + m.getTeam2Score(),
                    m.getMatchDate(), m.getStatus()
            });
        }
        if (lblCount != null)
            lblCount.setText("  Showing " + tableModel.getRowCount() + " matches   ");
    }

    private void clearFields() {
        matchIdField.setText(""); tournamentIdField.setText("");
        team1IdField.setText(""); team2IdField.setText("");
        matchDateField.setText(""); winnerIdField.setText("");
        team1ScoreField.setText(""); team2ScoreField.setText("");
        matchTable.clearSelection();
    }

    private JTextField lightField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Arial", Font.PLAIN, 12));
        f.setBackground(INPUT_BG);
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DIM),
                BorderFactory.createEmptyBorder(3, 7, 3, 7)));
        return f;
    }

    private JLabel fLbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Arial", Font.BOLD, 11));
        l.setForeground(TEXT_DIM);
        return l;
    }

    private JButton solidBtn(String text, Color bg, int width) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        if (width > 0) b.setPreferredSize(new Dimension(width, 30));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }
}