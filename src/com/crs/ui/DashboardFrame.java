package com.crs.ui;

import javax.swing.*;

import java.awt.*;

public class DashboardFrame extends JFrame {

    private int    userId;
    private String userName;
    private String role;

    public DashboardFrame(int userId, String userName, String role) {
        this.userId   = userId;
        this.userName = userName;
        this.role     = role;

        setTitle("CRS System - Dashboard");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 100, 200));
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JLabel welcome = new JLabel("Welcome, " + userName + " (" + role + ")");
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        topBar.add(welcome, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setForeground(new Color(30, 100, 200));
        logoutBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });
        topBar.add(logoutBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel semLabel = new JLabel("📚 Select Semester to Browse", SwingConstants.CENTER);
        semLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        center.add(semLabel, BorderLayout.NORTH);

        JPanel semGrid = new JPanel(new GridLayout(2, 4, 14, 14));
        semGrid.setBorder(BorderFactory.createEmptyBorder(16, 10, 16, 10));

        for (int i = 1; i <= 8; i++) {
            final int sem = i;
            JButton btn = new JButton("Semester " + i);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btn.setBackground(new Color(230, 240, 255));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> new BrowseFrame(userId, userName, role, sem).setVisible(true));
            semGrid.add(btn);
        }
        center.add(semGrid, BorderLayout.CENTER);

       
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 20, 18, 20));

        JButton browseAll = new JButton("🔍 Browse All Resources");
        browseAll.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        browseAll.addActionListener(e -> new BrowseFrame(userId, userName, role, 0).setVisible(true));

        JButton upload = new JButton("📤 Upload Resource");
        upload.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        upload.setBackground(new Color(30, 100, 200));
        upload.setForeground(Color.WHITE);
        upload.addActionListener(e -> new UploadFrame(userId, userName, role).setVisible(true));

        bottom.add(browseAll);
        bottom.add(upload);
        center.add(bottom, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);
        setVisible(true);
    }
}