package com.esportsclub.ui;

import com.esportsclub.model.Match;
import com.esportsclub.model.Team;
import com.esportsclub.model.Tournament;
import com.esportsclub.model.User;
import com.esportsclub.service.ReportManager;
import com.esportsclub.service.TeamService;
import com.esportsclub.service.TournamentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ReportPanel extends JPanel {

    private static final Color BG_MAIN    = new Color(248, 247, 255);
    private static final Color BG_PANEL   = new Color(255, 255, 255);
    private static final Color BG_TABLE   = new Color(255, 255, 255);
    private static final Color ACCENT     = new Color(124, 58, 237);
    private static final Color ORANGE     = new Color(249, 115, 22);
    private static final Color GREEN      = new Color(16, 185, 129);
    private static final Color BLUE       = new Color(59, 130, 246);
    private static final Color YELLOW     = new Color(245, 158, 11);
    private static final Color PINK       = new Color(236, 72, 153);
    private static final Color RED        = new Color(220, 38, 38);
    private static final Color TEXT_MAIN  = new Color(30, 27, 46);
    private static final Color TEXT_DIM   = new Color(120, 110, 150);
    private static final Color ROW_ODD    = new Color(252, 251, 255);
    private static final Color ROW_EVEN   = new Color(255, 255, 255);
    private static final Color ROW_SELECT = new Color(124, 58, 237, 15);
    private static final Color BORDER_DIM = new Color(220, 215, 240);
    private static final Color INPUT_BG   = new Color(250, 249, 255);

    private final ReportManager     reportManager     = new ReportManager();
    private final TeamService       teamService       = new TeamService();
    private final TournamentService tournamentService = new TournamentService();

    private JLabel cardMatchCount, cardUserCount, cardTournCount, cardPopTeam, cardWinTeam;

    private DefaultTableModel matchByTeamModel;
    private DefaultTableModel activeUserModel;
    private DefaultTableModel finishedTournModel;
    private JComboBox<String> cmbTeamSearch;

    private List<Team>       loadedTeams;
    private List<Tournament> loadedTournaments;

    public ReportPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_MAIN);
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildTabs(),   BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(BG_PANEL);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_DIM),
                new EmptyBorder(12, 16, 12, 16)));

        JLabel title = new JLabel("Reports & Statistics");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(ACCENT);
        bar.add(title, BorderLayout.WEST);

        JButton btnLoad = solidBtn("Load / Refresh All", ACCENT, 160);
        btnLoad.addActionListener(e -> loadAll());
        bar.add(btnLoad, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildTabs() {
        JPanel wrap = new JPanel(new BorderLayout(0, 10));
        wrap.setBackground(BG_MAIN);
        wrap.setBorder(new EmptyBorder(12, 14, 14, 14));

        wrap.add(buildCards(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.PLAIN, 13));
        tabs.setBackground(BG_PANEL);
        tabs.setBorder(null);
        tabs.addTab("  Matches by Team  ",     buildMatchByTeamTab());
        tabs.addTab("  Active Users  ",         buildActiveUserTab());
        tabs.addTab("  Finished Tournaments  ", buildTournamentTab());
        wrap.add(tabs, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildCards() {
        JPanel row = new JPanel(new GridLayout(1, 5, 12, 0));
        row.setBackground(BG_MAIN);
        row.setBorder(new EmptyBorder(0, 0, 10, 0));

        cardMatchCount = new JLabel("—", SwingConstants.CENTER);
        cardUserCount  = new JLabel("—", SwingConstants.CENTER);
        cardTournCount = new JLabel("—", SwingConstants.CENTER);
        cardPopTeam    = new JLabel("—", SwingConstants.CENTER);
        cardWinTeam    = new JLabel("—", SwingConstants.CENTER);

        row.add(statCard("Total Matches",       cardMatchCount, ORANGE));
        row.add(statCard("Active Users",         cardUserCount,  GREEN));
        row.add(statCard("Finished Tournaments", cardTournCount, YELLOW));
        row.add(statCard("Most Popular Team",    cardPopTeam,    BLUE));
        row.add(statCard("Most Winning Team",    cardWinTeam,    PINK));
        return row;
    }

    private JPanel statCard(String label, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                new EmptyBorder(14, 14, 14, 14)));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setForeground(TEXT_DIM);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setForeground(accent);
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);

        card.add(lbl,        BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildMatchByTeamTab() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(BG_PANEL);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel queryRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        queryRow.setBackground(BG_PANEL);
        queryRow.add(sLbl("Team:"));

        cmbTeamSearch = new JComboBox<>();
        cmbTeamSearch.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbTeamSearch.setBackground(INPUT_BG);
        cmbTeamSearch.setForeground(TEXT_MAIN);
        cmbTeamSearch.setPreferredSize(new Dimension(220, 30));
        queryRow.add(cmbTeamSearch);

        JButton btnSearch = solidBtn("Search Matches", BLUE, 140);
        btnSearch.addActionListener(e -> loadMatchesByTeam());
        queryRow.add(btnSearch);
        p.add(queryRow, BorderLayout.NORTH);

        String[] cols = {"Match ID", "Tournament", "Team 1", "Team 2", "Winner", "Score", "Date", "Status"};
        matchByTeamModel = readOnlyModel(cols);
        JScrollPane sp = new JScrollPane(styledTable(matchByTeamModel));
        sp.getViewport().setBackground(BG_TABLE);
        sp.setBackground(BG_TABLE);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_DIM));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildActiveUserTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_PANEL);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID", "Username", "Email", "Role", "Status"};
        activeUserModel = readOnlyModel(cols);
        JTable tbl = styledTable(activeUserModel);

        tbl.getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBackground(sel ? ROW_SELECT : (r % 2 == 0 ? ROW_EVEN : ROW_ODD));
                setForeground("ADMIN".equals(v) ? PINK : GREEN);
                setFont(getFont().deriveFont(Font.BOLD));
                return this;
            }
        });

        JScrollPane sp = new JScrollPane(tbl);
        sp.getViewport().setBackground(BG_TABLE);
        sp.setBackground(BG_TABLE);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_DIM));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildTournamentTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_PANEL);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID", "Tournament Name", "Game", "Max Teams", "Start Date", "End Date", "Status"};
        finishedTournModel = readOnlyModel(cols);
        JScrollPane sp = new JScrollPane(styledTable(finishedTournModel));
        sp.getViewport().setBackground(BG_TABLE);
        sp.setBackground(BG_TABLE);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_DIM));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void loadAll() {
        loadedTeams       = teamService.getAllTeams();
        loadedTournaments = tournamentService.getAllTournaments();

        cmbTeamSearch.removeAllItems();
        cmbTeamSearch.addItem("— Select Team —");
        for (Team t : loadedTeams)
            cmbTeamSearch.addItem(t.getName() + " [id=" + t.getId() + "]");

        cardMatchCount.setText(String.valueOf(reportManager.getTotalMatchCount()));

        List<User> activeUsers = reportManager.getActiveUsers();
        cardUserCount.setText(String.valueOf(activeUsers.size()));

        List<Tournament> finished = reportManager.getFinishedTournaments();
        cardTournCount.setText(String.valueOf(finished.size()));

        Team pop = reportManager.getMostPopularTeam();
        cardPopTeam.setText(pop != null ? pop.getName() : "N/A");

        Team win = reportManager.getMostWinningTeam();
        cardWinTeam.setText(win != null ? win.getName() : "N/A");

        activeUserModel.setRowCount(0);
        for (User u : activeUsers)
            activeUserModel.addRow(new Object[]{
                    u.getId(), u.getUsername(), u.getEmail(), u.getRole(), u.getStatus()});

        finishedTournModel.setRowCount(0);
        for (Tournament t : finished) {
            String gameName = loadedTournaments.stream()
                    .filter(lt -> lt.getId() == t.getId())
                    .findFirst()
                    .map(lt -> String.valueOf(lt.getGameId()))
                    .orElse("—");
            finishedTournModel.addRow(new Object[]{
                    t.getId(), t.getName(), t.getGameId(), t.getMaxTeams(),
                    t.getStartDate(), t.getEndDate(), t.getStatus()});
        }
    }

    private void loadMatchesByTeam() {
        Object selected = cmbTeamSearch.getSelectedItem();
        if (selected == null || selected.toString().startsWith("—")) {
            JOptionPane.showMessageDialog(this, "Please select a team.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int teamId;
        try {
            teamId = Integer.parseInt(selected.toString().replaceAll(".*\\[id=(\\d+)\\].*", "$1"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid team selection.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Match> matches = reportManager.getMatchesByTeam(teamId);
        matchByTeamModel.setRowCount(0);

        for (Match m : matches) {
            String tournName  = getTournamentName(m.getTournamentId());
            String team1Name  = getTeamName(m.getTeam1Id());
            String team2Name  = getTeamName(m.getTeam2Id());
            String winnerName = m.getWinnerId() == 0 ? "—" : getTeamName(m.getWinnerId());

            matchByTeamModel.addRow(new Object[]{
                    m.getId(),
                    tournName,
                    team1Name,
                    team2Name,
                    winnerName,
                    m.getTeam1Score() + " : " + m.getTeam2Score(),
                    m.getMatchDate(),
                    m.getStatus()
            });
        }

        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No matches found for this team.",
                    "No Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String getTeamName(int teamId) {
        if (loadedTeams == null) return "Team " + teamId;
        return loadedTeams.stream()
                .filter(t -> t.getId() == teamId)
                .map(Team::getName)
                .findFirst()
                .orElse("Team " + teamId);
    }

    private String getTournamentName(int tournId) {
        if (loadedTournaments == null) return "Tournament " + tournId;
        return loadedTournaments.stream()
                .filter(t -> t.getId() == tournId)
                .map(Tournament::getName)
                .findFirst()
                .orElse("Tournament " + tournId);
    }

    public void resetView() { loadAll(); }

    private DefaultTableModel readOnlyModel(String[] cols) {
        return new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    private JTable styledTable(DefaultTableModel m) {
        JTable t = new JTable(m) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) c.setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                return c;
            }
        };
        t.setFont(new Font("Arial", Font.PLAIN, 12));
        t.setRowHeight(28);
        t.setBackground(BG_TABLE);
        t.setForeground(TEXT_MAIN);
        t.setGridColor(BORDER_DIM);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setSelectionBackground(ROW_SELECT);
        t.setSelectionForeground(TEXT_MAIN);
        t.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        t.getTableHeader().setBackground(new Color(243, 240, 255));
        t.getTableHeader().setForeground(ACCENT);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return t;
    }

    private JLabel sLbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
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