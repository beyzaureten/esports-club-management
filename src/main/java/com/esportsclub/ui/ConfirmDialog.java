package com.esportsclub.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ConfirmDialog {

    private static final Color BG         = new Color(255, 255, 255);
    private static final Color OVERLAY    = new Color(30, 15, 80, 180);
    private static final Color ACCENT     = new Color(124, 58, 237);
    private static final Color RED        = new Color(220, 38, 38);
    private static final Color TEXT_MAIN  = new Color(30, 27, 46);
    private static final Color TEXT_DIM   = new Color(120, 110, 150);
    private static final Color BORDER_DIM = new Color(220, 215, 240);


    public static boolean showDeleteConfirm(Component parent, String title, String message) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), title,
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setSize(360, 180);
        dialog.setLocationRelativeTo(parent);

        final boolean[] result = {false};

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);
        main.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 38, 38, 80), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(254, 242, 242));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_DIM),
                new EmptyBorder(14, 20, 14, 20)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(RED);
        header.add(lblTitle, BorderLayout.WEST);

        main.add(header, BorderLayout.NORTH);

        JPanel msgPanel = new JPanel(new BorderLayout());
        msgPanel.setBackground(BG);
        msgPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblMsg = new JLabel("<html><body style='width: 280px'>" + message + "</body></html>");
        lblMsg.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMsg.setForeground(TEXT_DIM);
        msgPanel.add(lblMsg, BorderLayout.CENTER);

        main.add(msgPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(new Color(250, 249, 255));
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_DIM));

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 12));
        btnCancel.setBackground(BG);
        btnCancel.setForeground(TEXT_DIM);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DIM, 1),
                new EmptyBorder(6, 16, 6, 16)));
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> { result[0] = false; dialog.dispose(); });
        btnCancel.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnCancel.setBackground(new Color(248, 247, 255)); }
            @Override public void mouseExited(MouseEvent e)  { btnCancel.setBackground(BG); }
        });

        JButton btnDelete = new JButton("Delete");
        btnDelete.setFont(new Font("Arial", Font.BOLD, 12));
        btnDelete.setBackground(RED);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setBorderPainted(false);
        btnDelete.setPreferredSize(new Dimension(80, 32));
        btnDelete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener(e -> { result[0] = true; dialog.dispose(); });
        btnDelete.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnDelete.setBackground(new Color(185, 28, 28)); }
            @Override public void mouseExited(MouseEvent e)  { btnDelete.setBackground(RED); }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnDelete);
        main.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(main);
        dialog.setVisible(true);
        return result[0];
    }
}