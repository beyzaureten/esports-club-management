package com.esportsclub.ui;

import com.esportsclub.dao.TeamMemberDAO;
import com.esportsclub.factory.TeamBuilder;
import com.esportsclub.factory.TeamFactory;
import com.esportsclub.model.Game;
import com.esportsclub.model.Team;
import com.esportsclub.model.TeamMember;
import com.esportsclub.model.User;
import com.esportsclub.service.GameService;
import com.esportsclub.service.TeamService;
import com.esportsclub.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class TeamPanel extends JPanel {

    private static final Color BG_MAIN    = new Color(248, 247, 255);
    private static final Color BG_PANEL   = new Color(255, 255, 255);
    private static final Color BG_TABLE   = new Color(255, 255, 255);
    private static final Color BG_FORM    = new Color(250, 249, 255);
    private static final Color ACCENT     = new Color(124, 58, 237);
    private static final Color GREEN      = new Color(16, 185, 129);
    private static final Color RED        = new Color(220, 38, 38);
    private static final Color BLUE       = new Color(59, 130, 246);
    private static final Color YELLOW     = new Color(245, 158, 11);
    private static final Color PURPLE     = new Color(139, 92, 246);
    private static final Color ORANGE     = new Color(249, 115, 22);
    private static final Color TEXT_MAIN  = new Color(30, 27, 46);
    private static final Color TEXT_DIM   = new Color(120, 110, 150);
    private static final Color ROW_ODD    = new Color(252, 251, 255);
    private static final Color ROW_EVEN   = new Color(255, 255, 255);
    private static final Color ROW_SELECT = new Color(124, 58, 237, 20);
    private static final Color BORDER_DIM = new Color(220, 215, 240);
    private static final Color INPUT_BG   = new Color(250, 249, 255);

    private final TeamService   teamService = new TeamService();
    private final GameService   gameService = new GameService();
    private final UserService   userService = new UserService();
    private final TeamMemberDAO memberDAO   = new TeamMemberDAO();

    private JTable            teamTable;
    private DefaultTableModel teamModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPanel            teamTableWrapper;

    private JTable            memberTable;
    private DefaultTableModel memberModel;
    private JLabel            lblMemberTitle;

    private JTextField        fldSearch;
    private JComboBox<String> cmbStatus;
    private JComboBox<String> cmbGameFilter;

    private JTextField        fldId, fldName;
    private JComboBox<String> cmbGame;
    private JSpinner          spnCapacity;
    private JComboBox<String> cmbStatusForm;
    private JComboBox<String> cmbQuickType;

    private JComboBox<String> cmbAddUser;
    private List<User>        loadedUsers;

    private JLabel lblCount;

    public TeamPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_MAIN);
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildSplit(),   BorderLayout.CENTER);
        add(buildForm(),    BorderLayout.SOUTH);
        loadAll();
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(BG_PANEL);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_DIM),
                new EmptyBorder(12, 16, 12, 16)));

        JLabel title = new JLabel("Teams");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(ACCENT);
        bar.add(title, BorderLayout.WEST);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filters.setOpaque(false);

        filters.add(sLbl("Search:"));
        fldSearch = lightField(14);
        fldSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void changedUpdate(DocumentEvent e) {}
        });
        filters.add(fldSearch);

        filters.add(sLbl("Status:"));
        cmbStatus = lightCombo(new String[]{"All", "ACTIVE", "INACTIVE"}, 95);
        cmbStatus.addActionListener(e -> applyFilter());
        filters.add(cmbStatus);

        filters.add(sLbl("Game:"));
        cmbGameFilter = lightCombo(new String[]{}, 140);
        cmbGameFilter.addActionListener(e -> applyFilter());
        filters.add(cmbGameFilter);

        JButton btnRefresh = solidBtn("Refresh", BLUE, 90);
        btnRefresh.addActionListener(e -> loadAll());
        filters.add(btnRefresh);

        bar.add(filters, BorderLayout.EAST);
        return bar;
    }

    private JSplitPane buildSplit() {
        teamTableWrapper = buildTeamTable();
        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                teamTableWrapper, buildMemberPanel());
        sp.setResizeWeight(0.62);
        sp.setDividerSize(4);
        sp.setBackground(BORDER_DIM);
        sp.setBorder(null);
        return sp;
    }

    private JPanel buildTeamTable() {
        String[] cols = {"ID", "Team Name", "Game", "Members / Max", "Status"};
        teamModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return c == 0 ? Integer.class : String.class;
            }
        };

        teamTable = new JTable(teamModel) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) c.setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                return c;
            }
        };

        teamTable.setFont(new Font("Arial", Font.PLAIN, 13));
        teamTable.setRowHeight(32);
        teamTable.setBackground(BG_TABLE);
        teamTable.setForeground(TEXT_MAIN);
        teamTable.setGridColor(BORDER_DIM);
        teamTable.setShowHorizontalLines(true);
        teamTable.setShowVerticalLines(false);
        teamTable.setSelectionBackground(ROW_SELECT);
        teamTable.setSelectionForeground(TEXT_MAIN);
        teamTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teamTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        teamTable.getTableHeader().setBackground(new Color(243, 240, 255));
        teamTable.getTableHeader().setForeground(ACCENT);
        teamTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_DIM));
        teamTable.getTableHeader().setReorderingAllowed(false);

        int[] widths = {55, 220, 170, 130, 95};
        for (int i = 0; i < widths.length; i++)
            teamTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        teamTable.getColumnModel().getColumn(0).setCellRenderer(center);
        teamTable.getColumnModel().getColumn(3).setCellRenderer(center);

        teamTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(CENTER);
                setBackground(sel ? ROW_SELECT : (r % 2 == 0 ? ROW_EVEN : ROW_ODD));
                setForeground("ACTIVE".equals(v) ? GREEN : RED);
                setFont(getFont().deriveFont(Font.BOLD));
                return this;
            }
        });

        sorter = new TableRowSorter<>(teamModel);
        teamTable.setRowSorter(sorter);
        teamTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { fillForm(); loadMembers(); }
        });

        JScrollPane sp = new JScrollPane(teamTable);
        sp.setBackground(BG_TABLE);
        sp.getViewport().setBackground(BG_TABLE);
        sp.setBorder(null);

        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setBackground(BG_TABLE);
        GridBagConstraints ec = new GridBagConstraints();
        ec.gridx = 0; ec.gridy = GridBagConstraints.RELATIVE;
        ec.insets = new Insets(6, 0, 6, 0);
        JLabel emptyText = new JLabel("No teams found");
        emptyText.setFont(new Font("Arial", Font.BOLD, 15));
        emptyText.setForeground(ACCENT);
        emptyPanel.add(emptyText, ec);
        JLabel emptySub = new JLabel("Add a new team using the form below");
        emptySub.setFont(new Font("Arial", Font.PLAIN, 12));
        emptySub.setForeground(TEXT_DIM);
        emptyPanel.add(emptySub, ec);

        JPanel wrapper = new JPanel(new CardLayout());
        wrapper.add(sp, "table");
        wrapper.add(emptyPanel, "empty");
        wrapper.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_DIM));
        return wrapper;
    }

    private JPanel buildMemberPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_DIM));

        JPanel topRow = new JPanel(new BorderLayout(10, 0));
        topRow.setOpaque(false);
        topRow.setBorder(new EmptyBorder(6, 8, 4, 8));

        lblMemberTitle = new JLabel("  Team Members  —  select a team to view members");
        lblMemberTitle.setFont(new Font("Arial", Font.BOLD, 12));
        lblMemberTitle.setForeground(PURPLE);
        topRow.add(lblMemberTitle, BorderLayout.WEST);

        JPanel addMemberRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        addMemberRow.setOpaque(false);

        addMemberRow.add(sLbl("Add Member:"));
        cmbAddUser = lightCombo(new String[]{}, 160);
        addMemberRow.add(cmbAddUser);

        JButton btnAddMember = solidBtn("+ Add", ORANGE, 80);
        btnAddMember.addActionListener(e -> doAddMember());
        addMemberRow.add(btnAddMember);

        JButton btnRemoveMember = solidBtn("Remove", RED, 85);
        btnRemoveMember.addActionListener(e -> doRemoveMember());
        addMemberRow.add(btnRemoveMember);

        topRow.add(addMemberRow, BorderLayout.EAST);
        p.add(topRow, BorderLayout.NORTH);

        String[] cols = {"Member ID", "Username", "Join Date"};
        memberModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        memberTable = new JTable(memberModel);
        memberTable.setFont(new Font("Arial", Font.PLAIN, 12));
        memberTable.setRowHeight(26);
        memberTable.setBackground(BG_TABLE);
        memberTable.setForeground(TEXT_MAIN);
        memberTable.setGridColor(BORDER_DIM);
        memberTable.setShowHorizontalLines(true);
        memberTable.setShowVerticalLines(false);
        memberTable.setSelectionBackground(ROW_SELECT);
        memberTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        memberTable.getTableHeader().setBackground(new Color(243, 240, 255));
        memberTable.getTableHeader().setForeground(PURPLE);

        JScrollPane sp = new JScrollPane(memberTable);
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
        g.gridx = 2; g.weightx = 0.09; form.add(fLbl("Team Name *"), g);
        g.gridx = 3; g.weightx = 0.3; fldName = new JTextField(); styleField(fldName); form.add(fldName, g);
        g.gridx = 4; g.weightx = 0.08; form.add(fLbl("Game *"), g);
        g.gridx = 5; g.weightx = 0.2; cmbGame = lightCombo(new String[]{}, 0); form.add(cmbGame, g);

        g.gridy = 1;
        g.gridx = 0; g.weightx = 0.05; form.add(fLbl("Max Cap. *"), g);
        g.gridx = 1; g.weightx = 0.1;
        spnCapacity = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));
        spnCapacity.setFont(new Font("Arial", Font.PLAIN, 12));
        form.add(spnCapacity, g);
        g.gridx = 2; g.weightx = 0.09; form.add(fLbl("Status"), g);
        g.gridx = 3; g.weightx = 0.3;
        cmbStatusForm = lightCombo(new String[]{"ACTIVE", "INACTIVE"}, 0); form.add(cmbStatusForm, g);
        g.gridx = 4; g.weightx = 0.08; form.add(fLbl("Quick Type"), g);
        g.gridx = 5; g.weightx = 0.2;
        cmbQuickType = lightCombo(new String[]{
                "COMPETITIVE  (cap=5)", "CASUAL  (cap=10)", "TRAINING  (cap=3)"}, 0);
        form.add(cmbQuickType, g);

        wrap.add(form, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new BorderLayout());
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(0, 14, 10, 14));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JButton btnAdd      = solidBtn("+ Add",     GREEN,                    90);
        JButton btnUpdate   = solidBtn("Update",    BLUE,                     90);
        JButton btnDelete   = solidBtn("Delete",    RED,                      90);
        JButton btnClear    = solidBtn("Clear",     new Color(150, 140, 180), 80);
        JButton btnQuickAdd = solidBtn("Quick Add", YELLOW,                   95);

        btnAdd.addActionListener(e      -> doAdd());
        btnUpdate.addActionListener(e   -> doUpdate());
        btnDelete.addActionListener(e   -> doDelete());
        btnClear.addActionListener(e    -> clearForm());
        btnQuickAdd.addActionListener(e -> doQuickAdd());

        left.add(btnAdd); left.add(btnUpdate); left.add(btnDelete); left.add(btnClear);
        left.add(new JSeparator(JSeparator.VERTICAL) {{ setPreferredSize(new Dimension(1, 24)); }});
        left.add(btnQuickAdd);

        lblCount = new JLabel();
        lblCount.setFont(new Font("Arial", Font.ITALIC, 11));
        lblCount.setForeground(TEXT_DIM);

        btnRow.add(left, BorderLayout.WEST);
        btnRow.add(lblCount, BorderLayout.EAST);
        wrap.add(btnRow, BorderLayout.SOUTH);
        return wrap;
    }

    private void doAddMember() {
        if (fldId.getText().trim().isEmpty()) { warn("Please select a team first."); return; }
        Object selected = cmbAddUser.getSelectedItem();
        if (selected == null || selected.toString().startsWith("—")) {
            warn("Please select a user to add."); return;
        }
        try {
            int teamId = Integer.parseInt(fldId.getText().trim());
            int userId = Integer.parseInt(selected.toString().replaceAll(".*\\[id=(\\d+)\\].*", "$1"));

            int cur = memberDAO.countByTeamId(teamId);
            int max = (int) spnCapacity.getValue();
            if (cur >= max) { warn("Team is full! Max capacity: " + max); return; }

            List<TeamMember> existing = memberDAO.getByTeamId(teamId);
            boolean alreadyMember = existing.stream().anyMatch(m -> m.getUserId() == userId);
            if (alreadyMember) { warn("This user is already a member of this team."); return; }

            String joinDate = LocalDate.now().toString();
            TeamMember member = new TeamMember(0, teamId, userId, joinDate);
            if (memberDAO.insert(member)) {
                info("Member added successfully.");
                loadMembers(); loadAll();
            } else { error("Failed to add member."); }
        } catch (NumberFormatException ex) { error("Invalid ID."); }
    }

    private void doRemoveMember() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow < 0) { warn("Please select a member to remove."); return; }
        int memberId = Integer.parseInt(memberModel.getValueAt(selectedRow, 0).toString());
        String username = memberModel.getValueAt(selectedRow, 1).toString();

        boolean confirmed = ConfirmDialog.showDeleteConfirm(this,
                "Remove Member",
                "Are you sure you want to remove \"" + username + "\" from this team?");
        if (!confirmed) return;

        if (memberDAO.delete(memberId)) {
            info("Member removed.");
            loadMembers(); loadAll();
        } else { error("Failed to remove member."); }
    }

    private void doAdd() {
        String name = fldName.getText().trim();
        if (name.isEmpty()) { warn("Team name is required."); return; }
        Object gameItem = cmbGame.getSelectedItem();
        if (gameItem == null) { warn("Please select a game."); return; }
        int gameId = parseGameId(gameItem.toString());
        if (gameId < 0) { error("Selected game not found."); return; }

        Team team = new TeamBuilder()
                .setName(name).setGameId(gameId)
                .setMaxCapacity((int) spnCapacity.getValue())
                .setStatus(cmbStatusForm.getSelectedItem().toString())
                .build();

        if (team == null) { error("Invalid team data."); return; }
        if (teamService.addTeam(team)) { info("Team \"" + name + "\" added."); clearForm(); loadAll(); }
        else { error("Failed to add team."); }
    }

    private void doQuickAdd() {
        String name = fldName.getText().trim();
        if (name.isEmpty()) { warn("Enter a team name first."); return; }
        Object gameItem = cmbGame.getSelectedItem();
        if (gameItem == null) { warn("Please select a game."); return; }
        int gameId = parseGameId(gameItem.toString());
        if (gameId < 0) { error("Selected game not found."); return; }

        String typeStr = cmbQuickType.getSelectedItem().toString().split(" ")[0];
        Team team = TeamFactory.createTeam(typeStr, name, gameId);
        if (teamService.addTeam(team)) { info("Team \"" + name + "\" added as " + typeStr + "."); clearForm(); loadAll(); }
        else { error("Failed to add team."); }
    }

    private void doUpdate() {
        if (fldId.getText().trim().isEmpty()) { warn("Select a team first."); return; }
        String name = fldName.getText().trim();
        if (name.isEmpty()) { warn("Team name is required."); return; }
        Object gameItem = cmbGame.getSelectedItem();
        int gameId = gameItem != null ? parseGameId(gameItem.toString()) : -1;
        if (gameId < 0) { warn("Please select a game."); return; }
        try {
            Team t = new Team(Integer.parseInt(fldId.getText().trim()), name, gameId,
                    (int) spnCapacity.getValue(), cmbStatusForm.getSelectedItem().toString());
            if (teamService.updateTeam(t)) { info("Team updated."); clearForm(); loadAll(); }
        } catch (NumberFormatException ex) { error("Invalid ID."); }
    }

    private void doDelete() {
        if (fldId.getText().trim().isEmpty()) { warn("Select a team first."); return; }
        try {
            int id = Integer.parseInt(fldId.getText().trim());
            boolean hasMem = memberDAO.countByTeamId(id) > 0;
            String extra = hasMem ? "<br>Warning: this team has members." : "";
            boolean confirmed = ConfirmDialog.showDeleteConfirm(this,
                    "Delete Team",
                    "Are you sure you want to delete \"" + fldName.getText().trim() + "\"?" + extra);
            if (!confirmed) return;
            teamService.deleteTeam(id);
            info("Team deleted."); clearForm(); loadAll();
        } catch (NumberFormatException ex) { error("Invalid ID."); }
    }

    private void loadAll() {
        teamModel.setRowCount(0);
        List<Team> teams = teamService.getAllTeams();
        List<Game> games = gameService.getAllGames();
        loadedUsers = userService.getAllUsers();

        for (Team t : teams) {
            String gameName = games.stream()
                    .filter(g -> g.getId() == t.getGameId())
                    .map(Game::getName).findFirst()
                    .orElse("Unknown (" + t.getGameId() + ")");
            int cur = memberDAO.countByTeamId(t.getId());
            teamModel.addRow(new Object[]{t.getId(), t.getName(), gameName,
                    cur + " / " + t.getMaxCapacity(), t.getStatus()});
        }

        Object curGF = cmbGameFilter.getSelectedItem();
        cmbGameFilter.removeAllItems();
        cmbGameFilter.addItem("All Games");
        games.stream().map(Game::getName).sorted().forEach(cmbGameFilter::addItem);
        if (curGF != null) cmbGameFilter.setSelectedItem(curGF);

        Object curGC = cmbGame.getSelectedItem();
        cmbGame.removeAllItems();
        games.forEach(g -> cmbGame.addItem(g.getName() + "  [id=" + g.getId() + "]"));
        if (curGC != null) cmbGame.setSelectedItem(curGC);

        // Kullanıcı comboboxunu güncelle
        cmbAddUser.removeAllItems();
        cmbAddUser.addItem("— Select User —");
        for (User u : loadedUsers)
            cmbAddUser.addItem(u.getUsername() + " [id=" + u.getId() + "]");

        lblCount.setText("  Showing " + teamModel.getRowCount() + " teams   ");
        CardLayout cl = (CardLayout) teamTableWrapper.getLayout();
        cl.show(teamTableWrapper, teamModel.getRowCount() == 0 ? "empty" : "table");
    }

    private void applyFilter() {
        String kw     = fldSearch.getText().trim();
        String status = cmbStatus.getSelectedItem().toString();
        Object gf     = cmbGameFilter.getSelectedItem();
        String game   = (gf == null || "All Games".equals(gf)) ? "" : gf.toString();

        sorter.setRowFilter(RowFilter.andFilter(Arrays.asList(
                kw.isEmpty()         ? RowFilter.regexFilter(".*") : RowFilter.regexFilter("(?i)" + kw, 1, 2),
                "All".equals(status) ? RowFilter.regexFilter(".*") : RowFilter.regexFilter("^" + status + "$", 4),
                game.isEmpty()       ? RowFilter.regexFilter(".*") : RowFilter.regexFilter("(?i)" + game, 2)
        )));
        lblCount.setText("  Showing " + teamTable.getRowCount() + " of " + teamModel.getRowCount() + " teams   ");
        CardLayout cl = (CardLayout) teamTableWrapper.getLayout();
        cl.show(teamTableWrapper, teamTable.getRowCount() == 0 ? "empty" : "table");
    }

    private void loadMembers() {
        memberModel.setRowCount(0);
        if (fldId.getText().trim().isEmpty()) {
            lblMemberTitle.setText("  Team Members  —  select a team to view members");
            return;
        }
        try {
            int id = Integer.parseInt(fldId.getText().trim());
            List<TeamMember> members = memberDAO.getByTeamId(id);
            lblMemberTitle.setText("  Members of \"" + fldName.getText().trim()
                    + "\"  (" + members.size() + " member" + (members.size() != 1 ? "s" : "") + ")");
            for (TeamMember m : members) {
                String username = loadedUsers == null ? "User " + m.getUserId() :
                        loadedUsers.stream()
                                .filter(u -> u.getId() == m.getUserId())
                                .map(User::getUsername)
                                .findFirst()
                                .orElse("User " + m.getUserId());
                memberModel.addRow(new Object[]{m.getId(), username, m.getJoinDate()});
            }
        } catch (NumberFormatException ignored) {}
    }

    private void fillForm() {
        int row = teamTable.getSelectedRow();
        if (row < 0) return;
        int mr = teamTable.convertRowIndexToModel(row);
        fldId.setText(teamModel.getValueAt(mr, 0).toString());
        fldName.setText(teamModel.getValueAt(mr, 1).toString());
        String gName = teamModel.getValueAt(mr, 2).toString();
        for (int i = 0; i < cmbGame.getItemCount(); i++) {
            if (cmbGame.getItemAt(i).startsWith(gName)) { cmbGame.setSelectedIndex(i); break; }
        }
        String capStr = teamModel.getValueAt(mr, 3).toString().split("/")[1].trim();
        try { spnCapacity.setValue(Integer.parseInt(capStr)); } catch (Exception ignored) {}
        cmbStatusForm.setSelectedItem(teamModel.getValueAt(mr, 4).toString());
    }

    private void clearForm() {
        fldId.setText(""); fldName.setText("");
        if (cmbGame.getItemCount() > 0) cmbGame.setSelectedIndex(0);
        spnCapacity.setValue(5); cmbStatusForm.setSelectedIndex(0);
        teamTable.clearSelection(); memberModel.setRowCount(0);
        lblMemberTitle.setText("  Team Members  —  select a team to view members");
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

    private JTextField lightField(int cols) {
        JTextField f = new JTextField(cols);
        f.setFont(new Font("Arial", Font.PLAIN, 12));
        f.setBackground(INPUT_BG);
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DIM),
                BorderFactory.createEmptyBorder(3, 7, 3, 7)));
        return f;
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

    private void info(String m)  { JOptionPane.showMessageDialog(this, m, "Success", JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String m)  { JOptionPane.showMessageDialog(this, m, "Warning", JOptionPane.WARNING_MESSAGE); }
    private void error(String m) { JOptionPane.showMessageDialog(this, m, "Error",   JOptionPane.ERROR_MESSAGE); }
}