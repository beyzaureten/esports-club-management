package com.esportsclub.ui;

import com.esportsclub.model.Match;
import com.esportsclub.model.Team;
import com.esportsclub.model.Tournament;
import com.esportsclub.service.MatchService;
import com.esportsclub.service.TeamService;
import com.esportsclub.service.TournamentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    private final MatchService      matchService      = new MatchService();
    private final TeamService       teamService       = new TeamService();
    private final TournamentService tournamentService = new TournamentService();

    private JTable            matchTable;
    private DefaultTableModel tableModel;
    private JPanel            formPanel;

    private List<Match>      loadedMatches     = new ArrayList<>();
    private List<Team>       loadedTeams       = new ArrayList<>();
    private List<Tournament> loadedTournaments = new ArrayList<>();

    private JTextField        fldMatchId;
    private JComboBox<String> cmbTournament;
    private JComboBox<String> cmbTeam1;
    private JComboBox<String> cmbTeam2;
    private JComboBox<String> cmbWinner;
    private JSpinner          dateChooser;
    private JTextField        fldTeam1Score;
    private JTextField        fldTeam2Score;

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

    private JSpinner makeDateSpinner() {
        SpinnerDateModel m = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        JSpinner sp = new JSpinner(m);
        JSpinner.DateEditor ed = new JSpinner.DateEditor(sp, "yyyy-MM-dd");
        sp.setEditor(ed);
        sp.setFont(new Font("Arial", Font.PLAIN, 12));
        ed.getTextField().setBackground(INPUT_BG);
        ed.getTextField().setForeground(TEXT_MAIN);
        ed.getTextField().setFont(new Font("Arial", Font.PLAIN, 12));
        return sp;
    }

    private String fmt(JSpinner sp) {
        return SDF.format((Date) sp.getValue());
    }

    private void parse(JSpinner sp, String s) {
        try { sp.setValue(SDF.parse(s)); }
        catch (ParseException e) { sp.setValue(new Date()); }
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
        String[] columns = {"ID", "Tournament", "Team 1", "Team 2", "Winner", "Score", "Date", "Status"};
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

        int[] widths = {40, 160, 150, 150, 150, 80, 100, 90};
        for (int i = 0; i < widths.length; i++)
            matchTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

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

        matchTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBackground(sel ? ROW_SELECT : (r % 2 == 0 ? ROW_EVEN : ROW_ODD));
                String val = v != null ? v.toString() : "";
                if (val.equals("—")) { setForeground(TEXT_DIM); }
                else { setForeground(GREEN); setFont(getFont().deriveFont(Font.BOLD)); }
                return this;
            }
        });

        matchTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int selectedRow = matchTable.getSelectedRow();
            if (selectedRow < 0) return;

            int matchId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            Match selected = loadedMatches.stream()
                    .filter(m -> m.getId() == matchId)
                    .findFirst().orElse(null);

            if (selected != null) {
                fldMatchId.setText(String.valueOf(selected.getId()));
                selectTournamentInCombo(selected.getTournamentId());
                parse(dateChooser, selected.getMatchDate());
                selectTeamInCombo(cmbTeam1, selected.getTeam1Id());
                selectTeamInCombo(cmbTeam2, selected.getTeam2Id());
                updateWinnerCombo();
                if (selected.getWinnerId() == 0) {
                    cmbWinner.setSelectedIndex(0);
                } else {
                    selectTeamInCombo(cmbWinner, selected.getWinnerId());
                }
                if (selected.getWinnerId() != 0) {
                    fldTeam1Score.setText(String.valueOf(selected.getTeam1Score()));
                    fldTeam2Score.setText(String.valueOf(selected.getTeam2Score()));
                } else {
                    fldTeam1Score.setText("");
                    fldTeam2Score.setText("");
                }
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
        g.gridx = 1; g.weightx = 0.12;
        fldMatchId = lightField();
        fldMatchId.setEditable(false);
        fldMatchId.setBackground(new Color(240, 238, 250));
        inputPanel.add(fldMatchId, g);
        g.gridx = 2; g.weightx = 0.08; inputPanel.add(fLbl("Tournament *"), g);
        g.gridx = 3; g.weightx = 0.25;
        cmbTournament = new JComboBox<>();
        styleCombo(cmbTournament);
        cmbTournament.addActionListener(e -> onTournamentSelected());
        inputPanel.add(cmbTournament, g);
        g.gridx = 4; g.weightx = 0.08; inputPanel.add(fLbl("Match Date *"), g);
        g.gridx = 5; g.weightx = 0.15;
        dateChooser = makeDateSpinner();
        inputPanel.add(dateChooser, g);

        g.gridy = 1;
        g.gridx = 0; g.weightx = 0.08; inputPanel.add(fLbl("Team 1 *"), g);
        g.gridx = 1; g.weightx = 0.12;
        cmbTeam1 = teamCombo();
        cmbTeam1.addActionListener(e -> updateWinnerCombo());
        inputPanel.add(cmbTeam1, g);
        g.gridx = 2; g.weightx = 0.08; inputPanel.add(fLbl("Team 2 *"), g);
        g.gridx = 3; g.weightx = 0.25;
        cmbTeam2 = teamCombo();
        cmbTeam2.addActionListener(e -> updateWinnerCombo());
        inputPanel.add(cmbTeam2, g);
        g.gridx = 4; g.weightx = 0.08; inputPanel.add(fLbl("Winner"), g);
        g.gridx = 5; g.weightx = 0.15;
        cmbWinner = new JComboBox<>(new String[]{"— No winner yet —"});
        styleCombo(cmbWinner);
        inputPanel.add(cmbWinner, g);

        g.gridy = 2;
        g.gridx = 0; g.weightx = 0.08; inputPanel.add(fLbl("Team 1 Score"), g);
        g.gridx = 1; g.weightx = 0.12; fldTeam1Score = lightField(); inputPanel.add(fldTeam1Score, g);
        g.gridx = 2; g.weightx = 0.08; inputPanel.add(fLbl("Team 2 Score"), g);
        g.gridx = 3; g.weightx = 0.25; fldTeam2Score = lightField(); inputPanel.add(fldTeam2Score, g);

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

    private void onTournamentSelected() {
        int tournId = getSelectedTournamentId();
        cmbTeam1.removeAllItems();
        cmbTeam2.removeAllItems();
        cmbTeam1.addItem("— Select Team 1 —");
        cmbTeam2.addItem("— Select Team 2 —");

        if (tournId < 0) {
            for (Team t : loadedTeams) {
                String item = t.getName() + " [id=" + t.getId() + "]";
                cmbTeam1.addItem(item);
                cmbTeam2.addItem(item);
            }
        } else {
            List<com.esportsclub.model.TournamentTeam> regTeams =
                    tournamentService.getTeamsInTournament(tournId);

            if (regTeams.isEmpty()) {
                for (Team t : loadedTeams) {
                    String item = t.getName() + " [id=" + t.getId() + "]";
                    cmbTeam1.addItem(item);
                    cmbTeam2.addItem(item);
                }
            } else {
                for (com.esportsclub.model.TournamentTeam tt : regTeams) {
                    String teamName = getTeamName(tt.getTeamId());
                    String item = teamName + " [id=" + tt.getTeamId() + "]";
                    cmbTeam1.addItem(item);
                    cmbTeam2.addItem(item);
                }
            }
        }
        updateWinnerCombo();
    }

    private void addMatch() {
        int tournId = getSelectedTournamentId();
        if (tournId < 0) { warn("Please select a tournament."); return; }
        int team1Id = getSelectedTeamId(cmbTeam1);
        int team2Id = getSelectedTeamId(cmbTeam2);
        if (team1Id < 0 || team2Id < 0) { warn("Please select both teams."); return; }
        if (team1Id == team2Id)          { warn("Team 1 and Team 2 cannot be the same."); return; }

        String matchDate = fmt(dateChooser);
        Match match = new Match(0, tournId, team1Id, team2Id, 0, 0, 0, matchDate, "SCHEDULED");
        if (matchService.createMatch(match)) {
            info("Match added successfully.");
            clearFields(); loadMatches();
        } else { error("Failed to add match!"); }
    }

    private void enterResult() {
        if (fldMatchId.getText().trim().isEmpty()) { warn("Select a match first."); return; }
        try {
            int matchId    = Integer.parseInt(fldMatchId.getText().trim());
            int winnerId   = getSelectedTeamId(cmbWinner);
            int team1Score = Integer.parseInt(fldTeam1Score.getText().trim());
            int team2Score = Integer.parseInt(fldTeam2Score.getText().trim());
            if (winnerId < 0) { warn("Please select a winner."); return; }
            if (matchService.enterResult(matchId, winnerId, team1Score, team2Score)) {
                info("Result entered successfully.");
                clearFields(); loadMatches();
            } else { error("Failed to enter result!"); }
        } catch (NumberFormatException e) {
            error("Please fill all fields correctly!");
        }
    }

    private void deleteMatch() {
        if (fldMatchId.getText().trim().isEmpty()) { warn("Select a match first."); return; }
        try {
            int matchId = Integer.parseInt(fldMatchId.getText().trim());
            boolean confirmed = ConfirmDialog.showDeleteConfirm(this,
                    "Delete Match",
                    "Are you sure you want to delete this match?<br>This action cannot be undone.");
            if (confirmed) {
                matchService.deleteMatch(matchId);
                info("Match deleted.");
                clearFields(); loadMatches();
            }
        } catch (NumberFormatException e) {
            error("Please enter a valid Match ID!");
        }
    }

    private void loadMatches() {
        tableModel.setRowCount(0);
        loadedMatches     = matchService.getAllMatches();
        loadedTeams       = teamService.getAllTeams();
        loadedTournaments = tournamentService.getAllTournaments();

        Object curTourn = cmbTournament.getSelectedItem();
        cmbTournament.removeAllItems();
        cmbTournament.addItem("— Select Tournament —");
        for (Tournament t : loadedTournaments) {
            cmbTournament.addItem(t.getName() + " [id=" + t.getId() + "]");
        }
        if (curTourn != null) cmbTournament.setSelectedItem(curTourn);

        refreshAllTeamCombos();

        for (Match m : loadedMatches) {
            String tournName  = getTournamentName(m.getTournamentId());
            String team1Name  = getTeamName(m.getTeam1Id());
            String team2Name  = getTeamName(m.getTeam2Id());
            String winnerName = m.getWinnerId() == 0 ? "—" : getTeamName(m.getWinnerId());

            tableModel.addRow(new Object[]{
                    m.getId(), tournName,
                    team1Name, team2Name, winnerName,
                    m.getTeam1Score() + " - " + m.getTeam2Score(),
                    m.getMatchDate(), m.getStatus()
            });
        }
        if (lblCount != null)
            lblCount.setText("  Showing " + tableModel.getRowCount() + " matches   ");
    }

    private void refreshAllTeamCombos() {
        cmbTeam1.removeAllItems();
        cmbTeam2.removeAllItems();
        cmbTeam1.addItem("— Select Team 1 —");
        cmbTeam2.addItem("— Select Team 2 —");
        for (Team t : loadedTeams) {
            String item = t.getName() + " [id=" + t.getId() + "]";
            cmbTeam1.addItem(item);
            cmbTeam2.addItem(item);
        }
        updateWinnerCombo();
    }

    private void updateWinnerCombo() {
        String sel1 = cmbTeam1.getSelectedItem() != null ? cmbTeam1.getSelectedItem().toString() : "";
        String sel2 = cmbTeam2.getSelectedItem() != null ? cmbTeam2.getSelectedItem().toString() : "";
        cmbWinner.removeAllItems();
        cmbWinner.addItem("— No winner yet —");
        if (!sel1.startsWith("—")) cmbWinner.addItem(sel1);
        if (!sel2.startsWith("—")) cmbWinner.addItem(sel2);
    }

    private void selectTournamentInCombo(int tournId) {
        for (int i = 0; i < cmbTournament.getItemCount(); i++) {
            if (cmbTournament.getItemAt(i).contains("[id=" + tournId + "]")) {
                cmbTournament.setSelectedIndex(i); return;
            }
        }
    }

    private void selectTeamInCombo(JComboBox<String> combo, int teamId) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).contains("[id=" + teamId + "]")) {
                combo.setSelectedIndex(i); return;
            }
        }
    }

    private int getSelectedTournamentId() {
        Object selected = cmbTournament.getSelectedItem();
        if (selected == null) return -1;
        String s = selected.toString();
        if (s.startsWith("—")) return -1;
        try { return Integer.parseInt(s.replaceAll(".*\\[id=(\\d+)\\].*", "$1")); }
        catch (Exception e) { return -1; }
    }

    private int getSelectedTeamId(JComboBox<String> combo) {
        Object selected = combo.getSelectedItem();
        if (selected == null) return -1;
        String s = selected.toString();
        if (s.startsWith("—")) return -1;
        try { return Integer.parseInt(s.replaceAll(".*\\[id=(\\d+)\\].*", "$1")); }
        catch (Exception e) { return -1; }
    }

    private String getTeamName(int teamId) {
        return loadedTeams.stream()
                .filter(t -> t.getId() == teamId)
                .map(Team::getName)
                .findFirst()
                .orElse("Team " + teamId);
    }

    private String getTournamentName(int tournId) {
        return loadedTournaments.stream()
                .filter(t -> t.getId() == tournId)
                .map(Tournament::getName)
                .findFirst()
                .orElse("Tournament " + tournId);
    }

    private void clearFields() {
        fldMatchId.setText("");
        dateChooser.setValue(new Date());
        fldTeam1Score.setText("");
        fldTeam2Score.setText("");
        if (cmbTournament.getItemCount() > 0) cmbTournament.setSelectedIndex(0);
        if (cmbTeam1.getItemCount() > 0) cmbTeam1.setSelectedIndex(0);
        if (cmbTeam2.getItemCount() > 0) cmbTeam2.setSelectedIndex(0);
        if (cmbWinner.getItemCount() > 0) cmbWinner.setSelectedIndex(0);
        matchTable.clearSelection();
    }

    private JComboBox<String> teamCombo() {
        JComboBox<String> c = new JComboBox<>();
        styleCombo(c); return c;
    }

    private void styleCombo(JComboBox<String> c) {
        c.setFont(new Font("Arial", Font.PLAIN, 12));
        c.setBackground(INPUT_BG);
        c.setForeground(TEXT_MAIN);
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

    private void info(String m)  { JOptionPane.showMessageDialog(this, m, "Success", JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String m)  { JOptionPane.showMessageDialog(this, m, "Warning", JOptionPane.WARNING_MESSAGE); }
    private void error(String m) { JOptionPane.showMessageDialog(this, m, "Error",   JOptionPane.ERROR_MESSAGE); }
}