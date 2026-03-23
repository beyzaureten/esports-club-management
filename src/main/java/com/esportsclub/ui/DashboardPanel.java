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
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DashboardPanel extends JPanel {

    private static final Color BG_MAIN    = new Color(248, 247, 255);
    private static final Color BG_PANEL   = new Color(255, 255, 255);
    private static final Color ACCENT     = new Color(124, 58, 237);
    private static final Color GREEN      = new Color(16, 185, 129);
    private static final Color BLUE       = new Color(59, 130, 246);
    private static final Color YELLOW     = new Color(245, 158, 11);
    private static final Color PINK       = new Color(236, 72, 153);
    private static final Color ORANGE     = new Color(249, 115, 22);
    private static final Color TEXT_MAIN  = new Color(30, 27, 46);
    private static final Color TEXT_DIM   = new Color(120, 110, 150);
    private static final Color BORDER_DIM = new Color(220, 215, 240);

    private final ReportManager     reportManager     = new ReportManager();
    private final TeamService       teamService       = new TeamService();
    private final TournamentService tournamentService = new TournamentService();

    private final MainFrame mainFrame;

    // Stat kartları
    private JLabel lblTeamCount, lblUserCount, lblTournCount, lblMatchCount;

    // Son maçlar tablosu
    private JPanel recentMatchesPanel;

    // Hoş geldin etiketi
    private JLabel lblWelcome;

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_MAIN);
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    // =========================================================================
    // Header
    // =========================================================================
    private JPanel buildHeader() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(BG_PANEL);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_DIM),
                new EmptyBorder(14, 20, 14, 20)));

        lblWelcome = new JLabel("Welcome back!");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 22));
        lblWelcome.setForeground(ACCENT);
        bar.add(lblWelcome, BorderLayout.WEST);

        JButton btnRefresh = solidBtn("Refresh", BLUE, 90);
        btnRefresh.addActionListener(e -> loadData());
        bar.add(btnRefresh, BorderLayout.EAST);
        return bar;
    }

    // =========================================================================
    // Ana içerik
    // =========================================================================
    private JPanel buildContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_MAIN);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Stat kartları
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 14, 0));
        statsRow.setBackground(BG_MAIN);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        lblTeamCount  = new JLabel("—", SwingConstants.CENTER);
        lblUserCount  = new JLabel("—", SwingConstants.CENTER);
        lblTournCount = new JLabel("—", SwingConstants.CENTER);
        lblMatchCount = new JLabel("—", SwingConstants.CENTER);

        statsRow.add(statCard("Total Teams",       lblTeamCount,  ACCENT,  TEAM_PANEL()));
        statsRow.add(statCard("Active Users",       lblUserCount,  GREEN,   null));
        statsRow.add(statCard("Tournaments",        lblTournCount, YELLOW,  TOURN_PANEL()));
        statsRow.add(statCard("Total Matches",      lblMatchCount, PINK,    MATCH_PANEL()));

        content.add(statsRow);
        content.add(Box.createVerticalStrut(20));

        // Alt kısım: son maçlar + hızlı erişim
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 14, 0));
        bottomRow.setBackground(BG_MAIN);

        bottomRow.add(buildRecentMatches());
        bottomRow.add(buildQuickAccess());

        content.add(bottomRow);
        return content;
    }

    // =========================================================================
    // Stat kartı
    // =========================================================================
    private JPanel statCard(String label, JLabel valueLabel, Color accent, String panel) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(TEXT_DIM);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(accent);
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);

        card.add(lbl,        BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        if (panel != null) {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    card.setBackground(new Color(248, 247, 255));
                }
                @Override public void mouseExited(MouseEvent e) {
                    card.setBackground(BG_PANEL);
                }
                @Override public void mouseClicked(MouseEvent e) {
                    mainFrame.showPanel(panel);
                }
            });

            JLabel arrow = new JLabel("→");
            arrow.setFont(new Font("Arial", Font.BOLD, 14));
            arrow.setForeground(new Color(200, 190, 230));
            card.add(arrow, BorderLayout.EAST);
        }

        return card;
    }

    // =========================================================================
    // Son maçlar
    // =========================================================================
    private JPanel buildRecentMatches() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DIM, 1),
                new EmptyBorder(16, 16, 16, 16)));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = new JLabel("Recent Matches");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(TEXT_MAIN);
        JLabel viewAll = new JLabel("View all →");
        viewAll.setFont(new Font("Arial", Font.PLAIN, 12));
        viewAll.setForeground(ACCENT);
        viewAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewAll.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { mainFrame.showPanel(MATCH_PANEL()); }
            @Override public void mouseEntered(MouseEvent e) { viewAll.setForeground(ACCENT.darker()); }
            @Override public void mouseExited(MouseEvent e)  { viewAll.setForeground(ACCENT); }
        });
        titleRow.add(title,   BorderLayout.WEST);
        titleRow.add(viewAll, BorderLayout.EAST);
        card.add(titleRow, BorderLayout.NORTH);

        recentMatchesPanel = new JPanel();
        recentMatchesPanel.setLayout(new BoxLayout(recentMatchesPanel, BoxLayout.Y_AXIS));
        recentMatchesPanel.setOpaque(false);
        card.add(recentMatchesPanel, BorderLayout.CENTER);

        return card;
    }

    // =========================================================================
    // Hızlı erişim
    // =========================================================================
    private JPanel buildQuickAccess() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DIM, 1),
                new EmptyBorder(16, 16, 16, 16)));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = new JLabel("Upcoming Tournaments");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(TEXT_MAIN);
        JLabel viewAll = new JLabel("View all →");
        viewAll.setFont(new Font("Arial", Font.PLAIN, 12));
        viewAll.setForeground(GREEN);
        viewAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewAll.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { mainFrame.showPanel(TOURN_PANEL()); }
            @Override public void mouseEntered(MouseEvent e) { viewAll.setForeground(GREEN.darker()); }
            @Override public void mouseExited(MouseEvent e)  { viewAll.setForeground(GREEN); }
        });
        titleRow.add(title,   BorderLayout.WEST);
        titleRow.add(viewAll, BorderLayout.EAST);
        card.add(titleRow, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        List<Tournament> tournaments = tournamentService.getAllTournaments();
        int count = Math.min(tournaments.size(), 5);

        if (count == 0) {
            JLabel empty = new JLabel("No tournaments yet");
            empty.setFont(new Font("Arial", Font.PLAIN, 13));
            empty.setForeground(TEXT_DIM);
            empty.setBorder(new EmptyBorder(8, 0, 0, 0));
            listPanel.add(empty);
        } else {
            for (int i = 0; i < count; i++) {
                listPanel.add(tournamentRow(tournaments.get(i)));
                listPanel.add(Box.createVerticalStrut(6));
            }
        }

        card.add(listPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel tournamentRow(Tournament t) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(new Color(250, 249, 255));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DIM, 1),
                new EmptyBorder(8, 12, 8, 12)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel name = new JLabel(t.getName());
        name.setFont(new Font("Arial", Font.BOLD, 12));
        name.setForeground(TEXT_MAIN);

        JLabel status = new JLabel(t.getStatus());
        status.setFont(new Font("Arial", Font.BOLD, 11));
        status.setForeground("UPCOMING".equals(t.getStatus())  ? BLUE :
                "ONGOING".equals(t.getStatus())   ? GREEN : TEXT_DIM);

        JLabel date = new JLabel(t.getStartDate());
        date.setFont(new Font("Arial", Font.PLAIN, 11));
        date.setForeground(TEXT_DIM);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(date);
        right.add(status);

        row.add(name,  BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private JButton quickBtn(String text, Color bg, String panel) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(0, 40));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> mainFrame.showPanel(panel));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    // =========================================================================
    // Veri yükle
    // =========================================================================
    public void loadData(User user) {
        // Hoş geldin mesajı
        if (user != null) {
            lblWelcome.setText("Welcome back, " + user.getUsername() + "!");
        }

        // Stat kartları
        lblTeamCount.setText(String.valueOf(teamService.getAllTeams().size()));
        lblUserCount.setText(String.valueOf(reportManager.getActiveUsers().size()));
        lblTournCount.setText(String.valueOf(tournamentService.getAllTournaments().size()));
        lblMatchCount.setText(String.valueOf(reportManager.getTotalMatchCount()));

        // Son maçlar
        recentMatchesPanel.removeAll();
        List<Match> matches = reportManager.getMatchesByTeam(0);

        // Tüm maçları al
        List<Match> allMatches = new java.util.ArrayList<>();
        try {
            java.lang.reflect.Method m = reportManager.getClass().getDeclaredMethod("getTotalMatchCount");
        } catch (Exception ignored) {}

        // MatchDAO'dan son 5 maçı al
        com.esportsclub.dao.MatchDAO matchDAO = new com.esportsclub.dao.MatchDAO();
        List<Match> recentMatches = matchDAO.getAll();
        int count = Math.min(recentMatches.size(), 5);

        if (count == 0) {
            JLabel empty = new JLabel("No matches yet");
            empty.setFont(new Font("Arial", Font.PLAIN, 13));
            empty.setForeground(TEXT_DIM);
            empty.setBorder(new EmptyBorder(8, 0, 0, 0));
            recentMatchesPanel.add(empty);
        } else {
            for (int i = recentMatches.size() - 1; i >= recentMatches.size() - count; i--) {
                recentMatchesPanel.add(matchRow(recentMatches.get(i)));
                recentMatchesPanel.add(Box.createVerticalStrut(6));
            }
        }

        recentMatchesPanel.revalidate();
        recentMatchesPanel.repaint();
    }

    public void loadData() {
        loadData(null);
    }

    private JPanel matchRow(Match m) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(new Color(250, 249, 255));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DIM, 1),
                new EmptyBorder(8, 12, 8, 12)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel teams = new JLabel("Team " + m.getTeam1Id() + "  vs  Team " + m.getTeam2Id());
        teams.setFont(new Font("Arial", Font.BOLD, 12));
        teams.setForeground(TEXT_MAIN);

        JLabel score = new JLabel(m.getTeam1Score() + " : " + m.getTeam2Score());
        score.setFont(new Font("Arial", Font.BOLD, 13));
        score.setForeground(ACCENT);

        JLabel status = new JLabel(m.getStatus());
        status.setFont(new Font("Arial", Font.PLAIN, 11));
        status.setForeground("FINISHED".equals(m.getStatus()) ? GREEN :
                "ONGOING".equals(m.getStatus())  ? YELLOW : BLUE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(score);
        right.add(status);

        row.add(teams, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    // Panel sabitleri
    private String TEAM_PANEL()  { return MainFrame.TEAM_PANEL; }
    private String GAME_PANEL()  { return MainFrame.GAME_PANEL; }
    private String TOURN_PANEL() { return MainFrame.TOURNAMENT_PANEL; }
    private String MATCH_PANEL() { return MainFrame.MATCH_PANEL; }
    private String REPORT_PANEL(){ return MainFrame.REPORT_PANEL; }

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