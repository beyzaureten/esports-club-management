package com.esportsclub.ui;

import com.esportsclub.model.Game;
import com.esportsclub.service.GameService;

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
import java.util.Arrays;
import java.util.List;

public class GamePanel extends JPanel {

    private static final Color BG_MAIN    = new Color(248, 247, 255);
    private static final Color BG_PANEL   = new Color(255, 255, 255);
    private static final Color BG_TABLE   = new Color(255, 255, 255);
    private static final Color BG_FORM    = new Color(250, 249, 255);
    private static final Color ACCENT     = new Color(236, 72, 153);
    private static final Color GREEN      = new Color(16, 185, 129);
    private static final Color RED        = new Color(220, 38, 38);
    private static final Color BLUE       = new Color(59, 130, 246);
    private static final Color TEXT_MAIN  = new Color(30, 27, 46);
    private static final Color TEXT_DIM   = new Color(120, 110, 150);
    private static final Color ROW_ODD    = new Color(255, 252, 255);
    private static final Color ROW_EVEN   = new Color(255, 255, 255);
    private static final Color ROW_SELECT = new Color(236, 72, 153, 15);
    private static final Color BORDER_DIM = new Color(220, 215, 240);
    private static final Color INPUT_BG   = new Color(250, 249, 255);

    private static final String[] GENRES = {
            "FPS", "MOBA", "Battle Royale", "Sports", "RPG",
            "Strategy", "Racing", "Fighting", "Simulation", "Other"
    };
    private static final String[] MODES = {
            "1v1", "2v2", "3v3", "4v4", "5v5", "Solo", "Co-op", "Team", "Other"
    };

    private final GameService gameService = new GameService();

    private JTable               table;
    private DefaultTableModel    model;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField        fldSearch;
    private JComboBox<String> cmbGenreFilter;

    private JTextField        fldId, fldName;
    private JComboBox<String> cmbGenreForm;
    private JComboBox<String> cmbModeForm;
    private JLabel            lblCount;

    public GamePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_MAIN);
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
        add(buildForm(),    BorderLayout.SOUTH);
        loadAll();
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(BG_PANEL);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_DIM),
                new EmptyBorder(12, 16, 12, 16)));

        JLabel title = new JLabel("Games");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(ACCENT);
        bar.add(title, BorderLayout.WEST);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filters.setOpaque(false);

        filters.add(sLbl("Search:"));
        fldSearch = lightField(16);
        fldSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void changedUpdate(DocumentEvent e) {}
        });
        filters.add(fldSearch);

        filters.add(sLbl("Genre:"));
        cmbGenreFilter = lightCombo(new String[]{}, 120);
        cmbGenreFilter.addActionListener(e -> applyFilter());
        filters.add(cmbGenreFilter);

        JButton btnRefresh = solidBtn("Refresh", BLUE, 90);
        btnRefresh.addActionListener(e -> loadAll());
        filters.add(btnRefresh);

        bar.add(filters, BorderLayout.EAST);
        return bar;
    }

    private JScrollPane buildTable() {
        String[] cols = {"ID", "Game Name", "Genre", "Mode", "Teams Using"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return (c == 0 || c == 4) ? Integer.class : String.class;
            }
        };

        table = new JTable(model) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) c.setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                return c;
            }
        };

        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setBackground(BG_TABLE);
        table.setForeground(TEXT_MAIN);
        table.setGridColor(BORDER_DIM);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(ROW_SELECT);
        table.setSelectionForeground(TEXT_MAIN);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(255, 243, 250));
        table.getTableHeader().setForeground(ACCENT);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_DIM));
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        int[] widths = {55, 230, 130, 110, 105};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int c : new int[]{0, 4}) table.getColumnModel().getColumn(c).setCellRenderer(center);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillForm();
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(BG_TABLE);
        sp.getViewport().setBackground(BG_TABLE);
        sp.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER_DIM));
        return sp;
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
        g.gridx = 0; g.weightx = 0.06; form.add(fLbl("ID"), g);
        g.gridx = 1; g.weightx = 0.12;
        fldId = new JTextField(); fldId.setEditable(false);
        styleField(fldId); fldId.setBackground(new Color(240, 238, 250));
        form.add(fldId, g);
        g.gridx = 2; g.weightx = 0.1; form.add(fLbl("Game Name *"), g);
        g.gridx = 3; g.weightx = 0.35;
        fldName = new JTextField(); styleField(fldName); form.add(fldName, g);

        g.gridy = 1;
        g.gridx = 0; g.weightx = 0.06; form.add(fLbl("Genre *"), g);
        g.gridx = 1; g.weightx = 0.12;
        cmbGenreForm = lightCombo(GENRES, 0);
        form.add(cmbGenreForm, g);
        g.gridx = 2; g.weightx = 0.1; form.add(fLbl("Mode *"), g);
        g.gridx = 3; g.weightx = 0.35;
        cmbModeForm = lightCombo(MODES, 0);
        form.add(cmbModeForm, g);

        wrap.add(form, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new BorderLayout());
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(0, 14, 10, 14));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JButton btnAdd    = solidBtn("+ Add Game", GREEN,                    110);
        JButton btnUpdate = solidBtn("Update",     BLUE,                      90);
        JButton btnDelete = solidBtn("Delete",     RED,                       90);
        JButton btnClear  = solidBtn("Clear",      new Color(150, 140, 180),  80);

        btnAdd.addActionListener(e    -> doAdd());
        btnUpdate.addActionListener(e -> doUpdate());
        btnDelete.addActionListener(e -> doDelete());
        btnClear.addActionListener(e  -> clearForm());

        left.add(btnAdd); left.add(btnUpdate); left.add(btnDelete); left.add(btnClear);

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
        String genre = cmbGenreForm.getSelectedItem().toString();
        String mode  = cmbModeForm.getSelectedItem().toString();
        if (name.isEmpty()) { warn("Game name is required."); return; }
        if (gameService.addGame(name, genre, mode)) {
            info("Game \"" + name + "\" added."); clearForm(); loadAll();
        } else { error("Failed to add game. A game with this name may already exist."); }
    }

    private void doUpdate() {
        if (fldId.getText().trim().isEmpty()) { warn("Select a game first."); return; }
        String name  = fldName.getText().trim();
        String genre = cmbGenreForm.getSelectedItem().toString();
        String mode  = cmbModeForm.getSelectedItem().toString();
        if (name.isEmpty()) { warn("Game name is required."); return; }
        try {
            Game g = new Game(Integer.parseInt(fldId.getText().trim()), name, genre, mode);
            if (gameService.updateGame(g)) { info("Game updated."); clearForm(); loadAll(); }
            else { error("Update failed."); }
        } catch (NumberFormatException ex) { error("Invalid ID."); }
    }

    private void doDelete() {
        if (fldId.getText().trim().isEmpty()) { warn("Select a game first."); return; }
        boolean confirmed = ConfirmDialog.showDeleteConfirm(this,
                "Delete Game",
                "Are you sure you want to delete \"" + fldName.getText().trim() + "\"?<br>This action cannot be undone.");
        if (!confirmed) return;
        try {
            String err = gameService.deleteGame(Integer.parseInt(fldId.getText().trim()));
            if (err == null) { info("Game deleted."); clearForm(); loadAll(); }
            else { error(err); }
        } catch (NumberFormatException ex) { error("Invalid ID."); }
    }

    private void loadAll() {
        model.setRowCount(0);
        List<Game> games = gameService.getAllGames();
        for (Game g : games) {
            int teams = gameService.getTeamCountForGame(g.getId());
            model.addRow(new Object[]{g.getId(), g.getName(), g.getGenre(), g.getMode(), teams});
        }
        refreshGenreCombo(games);
        lblCount.setText("  Showing " + model.getRowCount() + " games   ");
    }

    private void applyFilter() {
        String kw     = fldSearch.getText().trim();
        Object genSel = cmbGenreFilter.getSelectedItem();
        String genre  = (genSel == null || "All Genres".equals(genSel)) ? "" : genSel.toString();

        RowFilter<DefaultTableModel, Object> f = RowFilter.andFilter(Arrays.asList(
                kw.isEmpty()    ? RowFilter.regexFilter(".*") : RowFilter.regexFilter("(?i)" + kw, 1, 2, 3),
                genre.isEmpty() ? RowFilter.regexFilter(".*") : RowFilter.regexFilter("(?i)^" + genre + "$", 2)
        ));
        sorter.setRowFilter(f);
        lblCount.setText("  Showing " + table.getRowCount() + " of " + model.getRowCount() + " games   ");
    }

    private void refreshGenreCombo(List<Game> games) {
        Object cur = cmbGenreFilter.getSelectedItem();
        cmbGenreFilter.removeAllItems();
        cmbGenreFilter.addItem("All Genres");
        games.stream().map(Game::getGenre).distinct().sorted().forEach(cmbGenreFilter::addItem);
        if (cur != null) cmbGenreFilter.setSelectedItem(cur);
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int mr = table.convertRowIndexToModel(row);
        fldId.setText(model.getValueAt(mr, 0).toString());
        fldName.setText(model.getValueAt(mr, 1).toString());
        String genre = model.getValueAt(mr, 2).toString();
        String mode  = model.getValueAt(mr, 3).toString();
        // Genre combobox'ta yoksa ekle
        boolean foundGenre = false;
        for (int i = 0; i < cmbGenreForm.getItemCount(); i++) {
            if (cmbGenreForm.getItemAt(i).equals(genre)) { cmbGenreForm.setSelectedIndex(i); foundGenre = true; break; }
        }
        if (!foundGenre) { cmbGenreForm.addItem(genre); cmbGenreForm.setSelectedItem(genre); }
        // Mode combobox'ta yoksa ekle
        boolean foundMode = false;
        for (int i = 0; i < cmbModeForm.getItemCount(); i++) {
            if (cmbModeForm.getItemAt(i).equals(mode)) { cmbModeForm.setSelectedIndex(i); foundMode = true; break; }
        }
        if (!foundMode) { cmbModeForm.addItem(mode); cmbModeForm.setSelectedItem(mode); }
    }

    private void clearForm() {
        fldId.setText(""); fldName.setText("");
        cmbGenreForm.setSelectedIndex(0);
        cmbModeForm.setSelectedIndex(0);
        table.clearSelection();
        fldName.requestFocus();
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