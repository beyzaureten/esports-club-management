package com.esportsclub.ui;

import com.esportsclub.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame {

    public static final String LOGIN_PANEL      = "LOGIN";
    public static final String DASHBOARD_PANEL  = "DASHBOARD";
    public static final String TEAM_PANEL       = "TEAM";
    public static final String GAME_PANEL       = "GAME";
    public static final String TOURNAMENT_PANEL = "TOURNAMENT";
    public static final String MATCH_PANEL      = "MATCH";
    public static final String REPORT_PANEL     = "REPORT";

    private static final Color SIDEBAR_BG   = new Color(30, 15, 80);
    private static final Color SIDEBAR_TOP  = new Color(20, 8, 60);
    private static final Color ACCENT       = new Color(124, 58, 237);
    private static final Color ACCENT_LIGHT = new Color(167, 139, 250);
    private static final Color BG_MAIN      = new Color(248, 247, 255);
    private static final Color TEXT_DIM     = new Color(150, 140, 180);
    private static final Color BORDER_DIM   = new Color(220, 215, 240);
    private static final Color STATUS_BG    = new Color(20, 8, 60);

    private User loggedInUser;
    private CardLayout cardLayout;
    private JPanel     cardPanel;
    private JPanel     sidebar;

    private JButton btnDash, btnTeam, btnGame, btnTournament, btnMatch, btnReport;
    private JButton activeBtn = null;

    private JPanel profileCard;
    private JLabel profileAvatar;
    private JLabel profileName;
    private JLabel profileRole;

    private JLabel lblUser, lblRole, lblTime;
    private Timer  clockTimer;

    private DashboardPanel  dashboardPanel;
    private TeamPanel       teamPanel;
    private GamePanel       gamePanel;
    private TournamentPanel tournamentPanel;
    private MatchPanel      matchPanel;
    private ReportPanel     reportPanel;

    public MainFrame() {
        super("E-Sports & Gaming Club Management System  v1.0");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1280, 780);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_MAIN);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { requestExit(); }
        });

        buildSidebar();
        buildCardPanel();
        buildStatusBar();
        startClock();

        showPanel(LOGIN_PANEL);
        sidebar.setVisible(false);
    }

    private void buildSidebar() {
        sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(220, 0));

        // Logo
        JPanel logoPanel = new JPanel(new GridLayout(3, 1, 0, 4));
        logoPanel.setBackground(SIDEBAR_TOP);
        logoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(124, 58, 237, 80)),
                new EmptyBorder(22, 16, 20, 16)));

        JLabel logoIcon = new JLabel("🎮", SwingConstants.CENTER);
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JLabel logoTitle = new JLabel("E-SPORTS CLUB", SwingConstants.CENTER);
        logoTitle.setFont(new Font("Arial", Font.BOLD, 14));
        logoTitle.setForeground(new Color(220, 210, 255));

        JLabel logoSub = new JLabel("Management System", SwingConstants.CENTER);
        logoSub.setFont(new Font("Arial", Font.PLAIN, 10));
        logoSub.setForeground(TEXT_DIM);

        logoPanel.add(logoIcon);
        logoPanel.add(logoTitle);
        logoPanel.add(logoSub);
        sidebar.add(logoPanel, BorderLayout.NORTH);

        // Nav
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(SIDEBAR_BG);
        navPanel.setBorder(new EmptyBorder(14, 0, 0, 0));

        btnDash       = navBtn("  Dashboard",    DASHBOARD_PANEL,  new Color(167, 139, 250));
        btnTeam       = navBtn("  Teams",        TEAM_PANEL,       new Color(139, 92, 246));
        btnGame       = navBtn("  Games",        GAME_PANEL,       new Color(236, 72, 153));
        btnTournament = navBtn("  Tournaments",  TOURNAMENT_PANEL, new Color(16, 185, 129));
        btnMatch      = navBtn("  Matches",      MATCH_PANEL,      new Color(245, 158, 11));
        btnReport     = navBtn("  Reports",      REPORT_PANEL,     new Color(59, 130, 246));

        navPanel.add(sectionLabel("OVERVIEW"));
        navPanel.add(btnDash);
        navPanel.add(Box.createVerticalStrut(6));
        navPanel.add(sectionLabel("MANAGEMENT"));
        navPanel.add(btnTeam);
        navPanel.add(btnGame);
        navPanel.add(btnTournament);
        navPanel.add(btnMatch);
        navPanel.add(Box.createVerticalStrut(6));
        navPanel.add(sectionLabel("ANALYTICS"));
        navPanel.add(btnReport);

        sidebar.add(navPanel, BorderLayout.CENTER);

        // Alt panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(SIDEBAR_BG);
        bottomPanel.setBorder(new EmptyBorder(0, 10, 16, 10));

        // Profil kartı
        profileCard = new JPanel(new BorderLayout(10, 0));
        profileCard.setBackground(new Color(22, 10, 60));
        profileCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(124, 58, 237, 50), 1),
                new EmptyBorder(10, 12, 10, 12)));
        profileCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        profileCard.setVisible(false);

        profileAvatar = new JLabel("A", SwingConstants.CENTER);
        profileAvatar.setFont(new Font("Arial", Font.BOLD, 14));
        profileAvatar.setForeground(Color.WHITE);
        profileAvatar.setOpaque(true);
        profileAvatar.setBackground(ACCENT);
        profileAvatar.setPreferredSize(new Dimension(34, 34));

        JPanel profileInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        profileInfo.setOpaque(false);

        profileName = new JLabel("admin");
        profileName.setFont(new Font("Arial", Font.BOLD, 12));
        profileName.setForeground(new Color(220, 210, 255));

        profileRole = new JLabel("ADMIN");
        profileRole.setFont(new Font("Arial", Font.PLAIN, 10));
        profileRole.setForeground(new Color(120, 100, 160));

        profileInfo.add(profileName);
        profileInfo.add(profileRole);

        profileCard.add(profileAvatar, BorderLayout.WEST);
        profileCard.add(profileInfo,   BorderLayout.CENTER);

        JPanel sep = new JPanel();
        sep.setBackground(new Color(50, 30, 100));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Arial", Font.BOLD, 12));
        btnLogout.setForeground(new Color(252, 165, 165));
        btnLogout.setBackground(new Color(60, 20, 40));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(239, 68, 68, 80), 1),
                new EmptyBorder(8, 16, 8, 16)));
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> requestLogout());
        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btnLogout.setBackground(new Color(80, 25, 50));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btnLogout.setBackground(new Color(60, 20, 40));
            }
        });

        bottomPanel.add(profileCard);
        bottomPanel.add(Box.createVerticalStrut(8));
        bottomPanel.add(sep);
        bottomPanel.add(Box.createVerticalStrut(8));
        bottomPanel.add(btnLogout);

        sidebar.add(bottomPanel, BorderLayout.SOUTH);
        add(sidebar, BorderLayout.WEST);
    }

    private JButton navBtn(String text, String panel, Color accent) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (this == activeBtn) {
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(accent);
                    g2.fillRect(0, 0, 4, getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Arial", Font.PLAIN, 13));
        b.setForeground(new Color(200, 190, 230));
        b.setBackground(new Color(0, 0, 0, 0));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(11, 22, 11, 22));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (b != activeBtn) b.setForeground(accent);
                b.repaint();
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (b != activeBtn) b.setForeground(new Color(200, 190, 230));
                b.repaint();
            }
        });

        b.addActionListener(e -> {
            if (panel.equals(REPORT_PANEL))    reportPanel.resetView();
            if (panel.equals(DASHBOARD_PANEL)) dashboardPanel.loadData(loggedInUser);
            showPanel(panel);
            setActiveBtn(b, accent);
        });

        return b;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel("  " + text);
        l.setFont(new Font("Arial", Font.BOLD, 9));
        l.setForeground(new Color(120, 100, 160));
        l.setBorder(new EmptyBorder(14, 12, 4, 12));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        return l;
    }

    private void setActiveBtn(JButton btn, Color accent) {
        if (activeBtn != null) {
            activeBtn.setForeground(new Color(200, 190, 230));
            activeBtn.setFont(new Font("Arial", Font.PLAIN, 13));
            activeBtn.repaint();
        }
        activeBtn = btn;
        activeBtn.setForeground(accent);
        activeBtn.setFont(new Font("Arial", Font.BOLD, 13));
        activeBtn.repaint();
    }

    private void buildCardPanel() {
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(BG_MAIN);

        dashboardPanel  = new DashboardPanel(this);
        teamPanel       = new TeamPanel();
        gamePanel       = new GamePanel();
        tournamentPanel = new TournamentPanel();
        matchPanel      = new MatchPanel();
        reportPanel     = new ReportPanel();

        cardPanel.add(new LoginPanel(this), LOGIN_PANEL);
        cardPanel.add(dashboardPanel,        DASHBOARD_PANEL);
        cardPanel.add(teamPanel,             TEAM_PANEL);
        cardPanel.add(gamePanel,             GAME_PANEL);
        cardPanel.add(tournamentPanel,       TOURNAMENT_PANEL);
        cardPanel.add(matchPanel,            MATCH_PANEL);
        cardPanel.add(reportPanel,           REPORT_PANEL);

        add(cardPanel, BorderLayout.CENTER);
    }

    private void buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(STATUS_BG);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(124, 58, 237, 80)),
                new EmptyBorder(4, 14, 4, 14)));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel appLabel = new JLabel("E-SPORTS CLUB");
        appLabel.setFont(new Font("Arial", Font.BOLD, 11));
        appLabel.setForeground(ACCENT_LIGHT);

        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 14));
        sep.setForeground(new Color(100, 80, 140));

        lblUser = new JLabel("Not logged in");
        lblUser.setFont(new Font("Arial", Font.PLAIN, 11));
        lblUser.setForeground(TEXT_DIM);

        lblRole = new JLabel("");
        lblRole.setFont(new Font("Arial", Font.BOLD, 11));

        left.add(appLabel);
        left.add(sep);
        left.add(lblUser);
        left.add(lblRole);

        lblTime = new JLabel();
        lblTime.setFont(new Font("Arial", Font.PLAIN, 11));
        lblTime.setForeground(TEXT_DIM);

        bar.add(left,    BorderLayout.WEST);
        bar.add(lblTime, BorderLayout.EAST);
        add(bar, BorderLayout.SOUTH);
    }

    private void startClock() {
        clockTimer = new Timer(1000, e ->
                lblTime.setText(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("dd MMM yyyy   HH:mm:ss")) + "   "));
        clockTimer.start();
    }

    public void onLoginSuccess(User user) {
        this.loggedInUser = user;
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());

        sidebar.setVisible(true);

        // Profil kartı
        profileAvatar.setText(String.valueOf(user.getUsername().charAt(0)).toUpperCase());
        profileName.setText(user.getUsername());
        profileRole.setText(user.getRole());
        profileRole.setForeground(isAdmin ? new Color(167, 139, 250) : new Color(16, 185, 129));
        profileCard.setVisible(true);

        // Admin kontrolü
        btnTeam.setEnabled(isAdmin);
        btnGame.setEnabled(isAdmin);
        btnTournament.setEnabled(true);
        btnMatch.setEnabled(true);
        btnReport.setEnabled(true);
        btnDash.setEnabled(true);

        if (!isAdmin) {
            Color dim = new Color(100, 90, 130);
            btnTeam.setForeground(dim);
            btnGame.setForeground(dim);
        }

        // Form görünürlüğü
        matchPanel.setAdminMode(isAdmin);
        tournamentPanel.setAdminMode(isAdmin);

        lblUser.setText("  " + user.getUsername() + "  ");
        lblRole.setText("[" + user.getRole() + "]");
        lblRole.setForeground(isAdmin ? new Color(167, 139, 250) : new Color(16, 185, 129));

        dashboardPanel.loadData(user);
        showPanel(DASHBOARD_PANEL);
        setActiveBtn(btnDash, new Color(167, 139, 250));
    }

    public void showPanel(String name) { cardLayout.show(cardPanel, name); }
    public User getLoggedInUser() { return loggedInUser; }

    private void requestLogout() {
        int r = JOptionPane.showConfirmDialog(this,
                "You will be logged out. Continue?",
                "Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;
        loggedInUser = null;
        lblUser.setText("Not logged in");
        lblUser.setForeground(TEXT_DIM);
        lblRole.setText("");
        profileCard.setVisible(false);
        sidebar.setVisible(false);
        activeBtn = null;
        showPanel(LOGIN_PANEL);
    }

    private void requestExit() {
        int r = JOptionPane.showConfirmDialog(this,
                "Exit the application?",
                "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;
        if (clockTimer != null) clockTimer.stop();
        dispose();
        System.exit(0);
    }
}