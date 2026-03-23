package com.esportsclub.ui;

import com.esportsclub.model.Game;
import com.esportsclub.model.Tournament;
import com.esportsclub.model.TournamentTeam;
import com.esportsclub.service.GameService;
import com.esportsclub.service.TournamentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TournamentPanel extends JPanel {

    private static final Color BG_MAIN    = new Color(248, 247, 255);
    private static final Color BG_PANEL   = new Color(255, 255, 255);
    private static final Color BG_TABLE   = new Color(255, 255, 255);
    private static final Color BG_FORM    = new Color(250, 249, 255);
    private static final Color ACCENT     = new Color(16, 185, 129);
    private static final Color GREEN      = new Color(16, 185, 129);
    private static final Color RED        = new Color(220, 38, 38);
    private static final Color BLUE       = new Color(59, 130, 246);
    private static final Color YELLOW     = new Color(245, 158, 11);
    private static final Color ORANGE     = new Color(249, 115, 22);
    private static final Color TEXT_MAIN  = new Color(30, 27, 46);
    private static final Color TEXT_DIM   = new Color(120, 110, 150);
    private static final Color ROW_ODD    = new Color(252, 255, 253);
    private static final Color ROW_EVEN   = new Color(255, 255, 255);
    private static final Color ROW_SELECT = new Color(16, 185, 129, 15);
    private static final Color BORDER_DIM = new Color(220, 215, 240);
    private static final Color INPUT_BG   = new Color(250, 249, 255);

    private final TournamentService tournamentService = new TournamentService();
    private final GameService       gameService       = new GameService();

    private JTable            tournTable;
    private DefaultTableModel tournModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPanel            tournTableWrapper;
    private JPanel            formPanel;

    private JTable            teamTable;
    private DefaultTableModel teamModel;
    private JLabel            lblTeamTitle;

    private JTextField        fldId, fldName, fldStartDate, fldEndDate;
    private JComboBox<String> cmbGame;
    private JSpinner          spnMaxTeams;
    private JComboBox<String> cmbStatus;
    private JTextField        fldTeamId;
    private JTextField        fldRegDate;

    private JLabel lblCount;

    public TournamentPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_MAIN);
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildSplit(),   BorderLayout.CENTER);
        formPanel = buildForm();
        add(formPanel,      BorderLayout.SOUTH);
        loadAll();
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

        JLabel title = new JLabel("Tournaments");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(ACCENT);
        bar.add(title, BorderLayout.WEST);

        JButton btnRefresh = solidBtn("Refresh", BLUE, 90);
        btnRefresh.addActionListener(e -> loadAll());
        bar.add(btnRefresh, BorderLayout.EAST);
        return bar;
    }

    private JSplitPane buildSplit() {
        tournTableWrapper = buildTournTable();
        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                tournTableWrapper, buildTeamPanel());
        sp.setResizeWeight(0.60);
        sp.setDividerSize(4);
        sp.setBackground(BORDER_DIM);
        sp.setBorder(null);
        return sp;
    }

    private JPanel buildTournTable() {
        String[] cols = {"ID", "Tournament Name", "Game", "Max Teams", "Start Date", "End Date", "Status"};
        tournModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return c == 0 ? Integer.class : String.class;
            }
        };

        tournTable = new JTable(tournModel) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) c.setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                return c;
            }
        };

        tournTable.setFont(new Font("Arial", Font.PLAIN, 13));
        tournTable.setRowHeight(32);
        tournTable.setBackground(BG_TABLE);
        tournTable.setForeground(TEXT_MAIN);
        tournTable.setGridColor(BORDER_DIM);
        tournTable.setShowHorizontalLines(true);
        tournTable.setShowVerticalLines(false);
        tournTable.setSelectionBackground(ROW_SELECT);
        tournTable.setSelectionForeground(TEXT_MAIN);
        tournTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tournTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tournTable.getTableHeader().setBackground(new Color(240, 255, 248));
        tournTable.getTableHeader().setForeground(ACCENT);
        tournTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_DIM));
        tournTable.getTableHeader().setReorderingAllowed(false);

        int[] widths = {50, 220, 150, 90, 110, 110, 110};
        for (int i = 0; i < widths.length; i++)
            tournTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tournTable.getColumnModel().getColumn(0).setCellRenderer(center);
        tournTable.getColumnModel().getColumn(3).setCellRenderer(center);

        tournTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(CENTER);
                setBackground(sel ? ROW_SELECT : (r % 2 == 0 ? ROW_EVEN : ROW_ODD));
                if ("UPCOMING".equals(v))      setForeground(BLUE);
                else if ("ONGOING".equals(v))  setForeground(GREEN);
                else if ("FINISHED".equals(v)) setForeground(RED);
                setFont(getFont().deriveFont(Font.BOLD));
                return this;
            }
        });

        sorter = new TableRowSorter<>(tournModel);
        tournTable.setRowSorter(sorter);
        tournTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { fillForm(); loadRegisteredTeams(); }
        });

        JScrollPane sp = new JScrollPane(tournTable);
        sp.setBackground(BG_TABLE);
        sp.getViewport().setBackground(BG_TABLE);
        sp.setBorder(null);

        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setBackground(BG_TABLE);
        GridBagConstraints ec = new GridBagConstraints();
        ec.gridx = 0; ec.gridy = GridBagConstraints.RELATIVE;
        ec.insets = new Insets(6, 0, 6, 0);

        JLabel emptyText = new JLabel("No tournaments found");
        emptyText.setFont(new Font("Arial", Font.BOLD, 15));
        emptyText.setForeground(ACCENT);
        emptyPanel.add(emptyText, ec);

        JLabel emptySub = new JLabel("Create a new tournament using the form below");
        emptySub.setFont(new Font("Arial", Font.PLAIN, 12));
        emptySub.setForeground(TEXT_DIM);
        emptyPanel.add(emptySub, ec);

        JPanel wrapper = new JPanel(new CardLayout());
        wrapper.add(sp, "table");
        wrapper.add(emptyPanel, "empty");
        wrapper.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_DIM));
        return wrapper;
    }

    private JPanel buildTeamPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_DIM));

        lblTeamTitle = new JLabel("  Registered Teams  —  select a tournament to view");
        lblTeamTitle.setFont(new Font("Arial", Font.BOLD, 12));
        lblTeamTitle.setForeground(YELLOW);
        lblTeamTitle.setBorder(new EmptyBorder(6, 8, 4, 0));
        p.add(lblTeamTitle, BorderLayout.NORTH);

        String[] cols = {"ID", "Tournament ID", "Team ID", "Registration Date"};
        teamModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        teamTable = new JTable(teamModel);
        teamTable.setFont(new Font("Arial", Font.PLAIN, 12));
        teamTable.setRowHeight(26);
        teamTable.setBackground(BG_TABLE);
        teamTable.setForeground(TEXT_MAIN);
        teamTable.setGridColor(BORDER_DIM);
        teamTable.setShowHorizontalLines(true);
        teamTable.setShowVerticalLines(false);
        teamTable.setSelectionBackground(ROW_SELECT);
        teamTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        teamTable.getTableHeader().setBackground(new Color(255, 252, 235));
        teamTable.getTableHeader().setForeground(YELLOW);

        JScrollPane sp = new JScrollPane(teamTable);
        sp.getViewport().setBackground(BG_TABLE);
        sp.setBorder(null);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildForm() {
        JPanel wrap = new JPanel(new BorderLayout(0, 0));
        wrap.setBackground(BG_FORM);
        wrap.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, ACCENT));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(10, 14, 6, 14));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 6, 4, 6);

        g.gridy = 0;
        g.gridx = 0; g.weightx = 0.05; form.add(fLbl("ID"), g);
        g.gridx = 1; g.weightx = 0.1;
        fldId = new JTextField(); fldId.setEditable(false);
        styleField(fldId); fldId.setBackground(new Color(240, 238, 250));
        form.add(fldId, g);
        g.gridx = 2; g.weightx = 0.1; form.add(fLbl("Name *"), g);
        g.gridx = 3; g.weightx = 0.25; fldName = new JTextField(); styleField(fldName); form.add(fldName, g);
        g.gridx = 4; g.weightx = 0.08; form.add(fLbl("Game *"), g);
        g.gridx = 5; g.weightx = 0.2;
        cmbGame = lightCombo(new String[]{}, 0); form.add(cmbGame, g);

        g.gridy = 1;
        g.gridx = 0; g.weightx = 0.05; form.add(fLbl("Max Teams *"), g);
        g.gridx = 1; g.weightx = 0.1;
        spnMaxTeams = new JSpinner(new SpinnerNumberModel(8, 2, 64, 2));
        spnMaxTeams.setFont(new Font("Arial", Font.PLAIN, 12));
        form.add(spnMaxTeams, g);
        g.gridx = 2; g.weightx = 0.1; form.add(fLbl("Start Date *"), g);
        g.gridx = 3; g.weightx = 0.25; fldStartDate = new JTextField(); styleField(fldStartDate); form.add(fldStartDate, g);
        g.gridx = 4; g.weightx = 0.08; form.add(fLbl("End Date *"), g);
        g.gridx = 5; g.weightx = 0.2; fldEndDate = new JTextField(); styleField(fldEndDate); form.add(fldEndDate, g);

        g.gridy = 2;
        g.gridx = 0; g.weightx = 0.05; form.add(fLbl("Status"), g);
        g.gridx = 1; g.weightx = 0.1;
        cmbStatus = lightCombo(new String[]{"UPCOMING", "ONGOING", "FINISHED"}, 0); form.add(cmbStatus, g);
        g.gridx = 2; g.weightx = 0.1; form.add(fLbl("Team ID (register)"), g);
        g.gridx = 3; g.weightx = 0.25; fldTeamId = new JTextField(); styleField(fldTeamId); form.add(fldTeamId, g);
        g.gridx = 4; g.weightx = 0.08; form.add(fLbl("Reg. Date"), g);
        g.gridx = 5; g.weightx = 0.2; fldRegDate = new JTextField(); styleField(fldRegDate); form.add(fldRegDate, g);

        wrap.add(form, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new BorderLayout());
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(0, 14, 10, 14));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JButton btnAdd    = solidBtn("+ Add",          GREEN,                    90);
        JButton btnUpdate = solidBtn("Update",         BLUE,                     90);
        JButton btnDelete = solidBtn("Delete",         RED,                      90);
        JButton btnClear  = solidBtn("Clear",          new Color(150, 140, 180), 80);
        JButton btnReg    = solidBtn("+ Register Team",ORANGE,                  130);

        btnAdd.addActionListener(e    -> doAdd());
        btnUpdate.addActionListener(e -> doUpdate());
        btnDelete.addActionListener(e -> doDelete());
        btnClear.addActionListener(e  -> clearForm());
        btnReg.addActionListener(e    -> doRegisterTeam());

        left.add(btnAdd); left.add(btnUpdate); left.add(btnDelete); left.add(btnClear);
        left.add(new JSeparator(JSeparator.VERTICAL) {{ setPreferredSize(new Dimension(1, 24)); }});
        left.add(btnReg);

        lblCount = new JLabel();
        lblCount.setFont(new Font("Arial", Font.ITALIC, 11));
        lblCount.setForeground(TEXT_DIM);

        btnRow.add(left, BorderLayout.WEST);
        btnRow.add(lblCount, BorderLayout.EAST);
        wrap.add(btnRow, BorderLayout.SOUTH);
        return wrap;
    }

    private void doAdd() {
        String name  = fldName.getText().trim();
        String start = fldStartDate.getText().trim();
        String end   = fldEndDate.getText().trim();
        if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
            warn("Please fill in all required fields."); return;
        }
        Object gameItem = cmbGame.getSelectedItem();
        if (gameItem == null) { warn("Please select a game."); return; }
        int gameId = parseGameId(gameItem.toString());
        if (gameId < 0) { error("Selected game not found."); return; }

        Tournament t = new Tournament(0, name, gameId,
                (int) spnMaxTeams.getValue(), start, end,
                cmbStatus.getSelectedItem().toString());

        if (tournamentService.createTournament(t)) {
            info("Tournament \"" + name + "\" created."); clearForm(); loadAll();
        } else { error("Failed to create tournament."); }
    }

    private void doUpdate() {
        if (fldId.getText().trim().isEmpty()) { warn("Select a tournament first."); return; }
        String name  = fldName.getText().trim();
        String start = fldStartDate.getText().trim();
        String end   = fldEndDate.getText().trim();
        if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
            warn("Please fill in all required fields."); return;
        }
        Object gameItem = cmbGame.getSelectedItem();
        int gameId = gameItem != null ? parseGameId(gameItem.toString()) : -1;
        if (gameId < 0) { warn("Please select a game."); return; }
        try {
            Tournament t = new Tournament(Integer.parseInt(fldId.getText().trim()), name, gameId,
                    (int) spnMaxTeams.getValue(), start, end,
                    cmbStatus.getSelectedItem().toString());
            if (tournamentService.updateTournament(t)) { info("Tournament updated."); clearForm(); loadAll(); }
        } catch (NumberFormatException ex) { error("Invalid ID."); }
    }

    private void doDelete() {
        if (fldId.getText().trim().isEmpty()) { warn("Select a tournament first."); return; }
        boolean confirmed = ConfirmDialog.showDeleteConfirm(this,
                "Delete Tournament",
                "Are you sure you want to delete \"" + fldName.getText().trim() + "\"?<br>This action cannot be undone.");
        if (!confirmed) return;
        try {
            tournamentService.deleteTournament(Integer.parseInt(fldId.getText().trim()));
            info("Tournament deleted."); clearForm(); loadAll();
        } catch (NumberFormatException ex) { error("Invalid ID."); }
    }

    private void doRegisterTeam() {
        if (fldId.getText().trim().isEmpty()) { warn("Select a tournament first."); return; }
        String teamIdStr = fldTeamId.getText().trim();
        String regDate   = fldRegDate.getText().trim();
        if (teamIdStr.isEmpty() || regDate.isEmpty()) {
            warn("Please enter Team ID and Registration Date."); return;
        }
        try {
            int tournId = Integer.parseInt(fldId.getText().trim());
            int teamId  = Integer.parseInt(teamIdStr);
            if (tournamentService.addTeamToTournament(tournId, teamId, regDate)) {
                info("Team registered successfully.");
                fldTeamId.setText(""); fldRegDate.setText("");
                loadRegisteredTeams();
            } else { error("Failed to register team. Tournament may be full."); }
        } catch (NumberFormatException ex) { error("Invalid ID format."); }
    }

    private void loadAll() {
        tournModel.setRowCount(0);
        List<Tournament> tournaments = tournamentService.getAllTournaments();
        List<Game> games = gameService.getAllGames();

        for (Tournament t : tournaments) {
            String gameName = games.stream()
                    .filter(g -> g.getId() == t.getGameId())
                    .map(Game::getName).findFirst()
                    .orElse("Unknown (" + t.getGameId() + ")");
            tournModel.addRow(new Object[]{t.getId(), t.getName(), gameName,
                    t.getMaxTeams(), t.getStartDate(), t.getEndDate(), t.getStatus()});
        }

        Object curGC = cmbGame.getSelectedItem();
        cmbGame.removeAllItems();
        games.forEach(g -> cmbGame.addItem(g.getName() + "  [id=" + g.getId() + "]"));
        if (curGC != null) cmbGame.setSelectedItem(curGC);

        lblCount.setText("  Showing " + tournModel.getRowCount() + " tournaments   ");

        CardLayout cl = (CardLayout) tournTableWrapper.getLayout();
        cl.show(tournTableWrapper, tournModel.getRowCount() == 0 ? "empty" : "table");
    }

    private void loadRegisteredTeams() {
        teamModel.setRowCount(0);
        if (fldId.getText().trim().isEmpty()) {
            lblTeamTitle.setText("  Registered Teams  —  select a tournament to view");
            return;
        }
        try {
            int id = Integer.parseInt(fldId.getText().trim());
            List<TournamentTeam> teams = tournamentService.getTeamsInTournament(id);
            lblTeamTitle.setText("  Registered Teams for \"" + fldName.getText().trim()
                    + "\"  (" + teams.size() + " team" + (teams.size() != 1 ? "s" : "") + ")");
            for (TournamentTeam tt : teams)
                teamModel.addRow(new Object[]{tt.getId(), tt.getTournamentId(), tt.getTeamId(), tt.getRegistrationDate()});
        } catch (NumberFormatException ignored) {}
    }

    private void fillForm() {
        int row = tournTable.getSelectedRow();
        if (row < 0) return;
        int mr = tournTable.convertRowIndexToModel(row);
        fldId.setText(tournModel.getValueAt(mr, 0).toString());
        fldName.setText(tournModel.getValueAt(mr, 1).toString());
        String gName = tournModel.getValueAt(mr, 2).toString();
        for (int i = 0; i < cmbGame.getItemCount(); i++) {
            if (cmbGame.getItemAt(i).startsWith(gName)) { cmbGame.setSelectedIndex(i); break; }
        }
        spnMaxTeams.setValue(Integer.parseInt(tournModel.getValueAt(mr, 3).toString()));
        fldStartDate.setText(tournModel.getValueAt(mr, 4).toString());
        fldEndDate.setText(tournModel.getValueAt(mr, 5).toString());
        cmbStatus.setSelectedItem(tournModel.getValueAt(mr, 6).toString());
    }

    private void clearForm() {
        fldId.setText(""); fldName.setText("");
        fldStartDate.setText(""); fldEndDate.setText("");
        fldTeamId.setText(""); fldRegDate.setText("");
        if (cmbGame.getItemCount() > 0) cmbGame.setSelectedIndex(0);
        spnMaxTeams.setValue(8); cmbStatus.setSelectedIndex(0);
        tournTable.clearSelection(); teamModel.setRowCount(0);
        lblTeamTitle.setText("  Registered Teams  —  select a tournament to view");
        fldName.requestFocus();
    }

    private int parseGameId(String item) {
        try { return Integer.parseInt(item.replaceAll(".*\\[id=(\\d+)\\].*", "$1")); }
        catch (Exception e) { return -1; }
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("Arial", Font.PLAIN, 12));
        f.setBackground(INPUT_BG);
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DIM),
                BorderFactory.createEmptyBorder(3, 7, 3, 7)));
    }

    private JComboBox<String> lightCombo(String[] items, int width) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(new Font("Arial", Font.PLAIN, 12));
        c.setBackground(INPUT_BG);
        c.setForeground(TEXT_MAIN);
        if (width > 0) c.setPreferredSize(new Dimension(width, 28));
        return c;
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