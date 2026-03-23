package com.esportsclub.ui;

import com.esportsclub.model.User;
import com.esportsclub.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginPanel extends JPanel {

    private static final Color BG          = new Color(248, 247, 255);
    private static final Color CARD_BG     = new Color(255, 255, 255);
    private static final Color HEADER_BG   = new Color(30, 15, 80);
    private static final Color ACCENT      = new Color(124, 58, 237);
    private static final Color ACCENT2     = new Color(236, 72, 153);
    private static final Color ERROR_C     = new Color(220, 38, 38);
    private static final Color TEXT_MAIN   = new Color(30, 27, 46);
    private static final Color TEXT_DIM    = new Color(120, 110, 150);
    private static final Color INPUT_BG    = new Color(250, 249, 255);
    private static final Color INPUT_BDR   = new Color(196, 181, 253);

    private final MainFrame   mainFrame;
    private final UserService userService;

    private JTextField     fldLoginUser;
    private JPasswordField fldLoginPass;
    private JButton        btnLogin;
    private JLabel         lblLoginMsg;

    private JTextField     fldRegUser;
    private JTextField     fldRegEmail;
    private JPasswordField fldRegPass;
    private JPasswordField fldRegConfirm;
    private JLabel         lblRegMsg;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame   = mainFrame;
        this.userService = new UserService();
        setLayout(new GridBagLayout());
        setBackground(BG);
        buildUI();
    }

    private void buildUI() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setPreferredSize(new Dimension(460, 545));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(196, 181, 253), 1),
                BorderFactory.createEmptyBorder(0, 0, 20, 0)));

        card.add(buildCardHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Arial", Font.BOLD, 13));
        tabs.setBackground(CARD_BG);
        tabs.setBorder(new EmptyBorder(8, 10, 0, 10));
        tabs.addTab("  Login  ",    buildLoginTab());
        tabs.addTab("  Register  ", buildRegisterTab());
        card.add(tabs, BorderLayout.CENTER);

        add(card);
    }

    private JPanel buildCardHeader() {
        JPanel header = new JPanel(new GridLayout(3, 1, 0, 6));
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(22, 24, 22, 24));

        JLabel icon = new JLabel("🎮", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));

        JLabel title = new JLabel("E-SPORTS CLUB", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(220, 210, 255));

        JLabel sub = new JLabel("Management System  ·  MISY1102  ·  Spring 2026", SwingConstants.CENTER);
        sub.setFont(new Font("Arial", Font.PLAIN, 11));
        sub.setForeground(new Color(150, 130, 190));

        header.add(icon);
        header.add(title);
        header.add(sub);
        return header;
    }

    private JPanel buildLoginTab() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CARD_BG);
        p.setBorder(new EmptyBorder(22, 36, 12, 36));
        GridBagConstraints g = gbc();

        g.gridy = 0; p.add(lbl("Username"), g);
        g.gridy = 1; fldLoginUser = textField(); p.add(fldLoginUser, g);

        g.gridy = 2; g.insets = ins(14, 0, 4, 0); p.add(lbl("Password"), g);
        g.gridy = 3; g.insets = ins(0, 0, 0, 0);
        p.add(passwordRow(fldLoginPass = pwField()), g);

        g.gridy = 4; g.insets = ins(5, 0, 0, 0);
        lblLoginMsg = msgLabel();
        p.add(lblLoginMsg, g);

        g.gridy = 5; g.insets = ins(18, 0, 0, 0);
        btnLogin = primaryBtn("LOGIN", ACCENT);
        btnLogin.addActionListener(e -> doLogin());
        p.add(btnLogin, g);

        g.gridy = 6; g.insets = ins(12, 0, 0, 0);
        JLabel hint = new JLabel("Demo: admin / admin123", SwingConstants.CENTER);
        hint.setFont(new Font("Arial", Font.ITALIC, 11));
        hint.setForeground(new Color(180, 170, 210));
        p.add(hint, g);

        ActionListener doLogin = e -> doLogin();
        fldLoginUser.addActionListener(doLogin);
        fldLoginPass.addActionListener(doLogin);
        return p;
    }

    private JPanel buildRegisterTab() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CARD_BG);
        p.setBorder(new EmptyBorder(18, 36, 12, 36));
        GridBagConstraints g = gbc();

        g.gridy = 0; p.add(lbl("Username  (min. 3 characters)"), g);
        g.gridy = 1; fldRegUser = textField(); p.add(fldRegUser, g);

        g.gridy = 2; g.insets = ins(10, 0, 4, 0); p.add(lbl("Email Address"), g);
        g.gridy = 3; g.insets = ins(0, 0, 0, 0); fldRegEmail = textField(); p.add(fldRegEmail, g);

        g.gridy = 4; g.insets = ins(10, 0, 4, 0); p.add(lbl("Password  (min. 6 characters)"), g);
        g.gridy = 5; g.insets = ins(0, 0, 0, 0);
        p.add(passwordRow(fldRegPass = pwField()), g);

        g.gridy = 6; g.insets = ins(10, 0, 4, 0); p.add(lbl("Confirm Password"), g);
        g.gridy = 7; g.insets = ins(0, 0, 0, 0);
        p.add(passwordRow(fldRegConfirm = pwField()), g);

        g.gridy = 8; g.insets = ins(4, 0, 0, 0);
        lblRegMsg = msgLabel();
        p.add(lblRegMsg, g);

        g.gridy = 9; g.insets = ins(14, 0, 0, 0);
        JButton btnReg = primaryBtn("CREATE ACCOUNT", ACCENT2);
        btnReg.addActionListener(e -> doRegister());
        p.add(btnReg, g);
        return p;
    }

    private void doLogin() {
        lblLoginMsg.setText(" ");
        String username = fldLoginUser.getText().trim();
        String password = new String(fldLoginPass.getPassword());

        if (username.isEmpty()) { setMsg(lblLoginMsg, "Username is required.", true); return; }
        if (password.isEmpty()) { setMsg(lblLoginMsg, "Password is required.", true); return; }

        btnLogin.setEnabled(false);
        btnLogin.setText("Connecting...");

        new SwingWorker<User, Void>() {
            @Override protected User doInBackground() {
                return userService.login(username, password);
            }
            @Override protected void done() {
                btnLogin.setEnabled(true);
                btnLogin.setText("LOGIN");
                try {
                    User user = get();
                    if (user == null) {
                        setMsg(lblLoginMsg, "Invalid username or password.", true);
                        fldLoginPass.setText("");
                        fldLoginPass.requestFocus();
                        return;
                    }
                    if ("INACTIVE".equalsIgnoreCase(user.getStatus())) {
                        setMsg(lblLoginMsg, "Your account is inactive. Contact admin.", true);
                        return;
                    }
                    fldLoginUser.setText("");
                    fldLoginPass.setText("");
                    lblLoginMsg.setText(" ");
                    mainFrame.onLoginSuccess(user);
                } catch (Exception ex) {
                    setMsg(lblLoginMsg, "Database connection error.", true);
                }
            }
        }.execute();
    }

    private void doRegister() {
        lblRegMsg.setText(" ");
        String username = fldRegUser.getText().trim();
        String email    = fldRegEmail.getText().trim();
        String pass     = new String(fldRegPass.getPassword());
        String confirm  = new String(fldRegConfirm.getPassword());

        if (username.isEmpty())    { setMsg(lblRegMsg, "Username is required.", true); return; }
        if (username.length() < 3) { setMsg(lblRegMsg, "Username must be at least 3 characters.", true); return; }
        if (email.isEmpty())       { setMsg(lblRegMsg, "Email is required.", true); return; }
        if (!isValidEmail(email))  { setMsg(lblRegMsg, "Enter a valid email address.", true); return; }
        if (pass.length() < 6)     { setMsg(lblRegMsg, "Password must be at least 6 characters.", true); return; }
        if (!pass.equals(confirm)) { setMsg(lblRegMsg, "Passwords do not match.", true); return; }

        boolean ok = userService.register(username, pass, email);
        if (ok) {
            setMsg(lblRegMsg, "Account created! You can now log in.", false);
            fldRegUser.setText(""); fldRegEmail.setText("");
            fldRegPass.setText(""); fldRegConfirm.setText("");
        } else {
            setMsg(lblRegMsg, "Registration failed. Username or email may already exist.", true);
        }
    }

    private JPanel passwordRow(JPasswordField pf) {
        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.setOpaque(false);
        JToggleButton toggle = new JToggleButton("Show");
        toggle.setFont(new Font("Arial", Font.PLAIN, 11));
        toggle.setFocusPainted(false);
        toggle.setBackground(INPUT_BG);
        toggle.setForeground(TEXT_DIM);
        toggle.setBorder(BorderFactory.createLineBorder(INPUT_BDR));
        toggle.setPreferredSize(new Dimension(56, 36));
        toggle.addActionListener(e -> {
            pf.setEchoChar(toggle.isSelected() ? (char) 0 : '•');
            toggle.setText(toggle.isSelected() ? "Hide" : "Show");
        });
        row.add(pf,     BorderLayout.CENTER);
        row.add(toggle, BorderLayout.EAST);
        return row;
    }

    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0; g.insets = ins(0, 0, 4, 0);
        return g;
    }

    private Insets ins(int t, int l, int b, int r) { return new Insets(t, l, b, r); }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setForeground(TEXT_DIM);
        return l;
    }

    private JTextField textField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(360, 36));
        f.setBackground(INPUT_BG);
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BDR),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        return f;
    }

    private JPasswordField pwField() {
        JPasswordField f = new JPasswordField();
        f.setEchoChar('•');
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(360, 36));
        f.setBackground(INPUT_BG);
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BDR),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        return f;
    }

    private JButton primaryBtn(String text, Color accent) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setBackground(accent);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(360, 42));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(accent.darker()); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(accent); }
        });
        return b;
    }

    private JLabel msgLabel() {
        JLabel l = new JLabel(" ", SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.PLAIN, 11));
        return l;
    }

    private void setMsg(JLabel lbl, String msg, boolean isError) {
        lbl.setText(msg);
        lbl.setForeground(isError ? ERROR_C : ACCENT);
    }

    private boolean isValidEmail(String email) {
        int at  = email.indexOf('@');
        int dot = email.lastIndexOf('.');
        return at > 0 && dot > at + 1 && dot < email.length() - 1;
    }
}