package com.crs.admin;
import com.crs.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.crs.db.DBConnection;

import java.awt.*;
import java.sql.*;
import java.io.File;

public class AdminPanel extends JFrame {

    JTable table;
    DefaultTableModel model;

    public AdminPanel() {
        setTitle("Admin Panel - CRS");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 🔵 TOP PANEL (HEADER)
        JLabel title = new JLabel("Admin Control Panel", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setOpaque(true);
        title.setBackground(new Color(30, 100, 200));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // 📊 TABLE
        model = new DefaultTableModel();
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        model.addColumn("ID");
        model.addColumn("Title");
        model.addColumn("User Name");   // 🔥 NEW
        model.addColumn("User ID");

        loadData();

        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);

        // 🔘 BUTTON PANEL
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);

        JButton deleteBtn = new JButton("Delete File");
        JButton blockBtn = new JButton("Block User");
        JButton refreshBtn = new JButton("Refresh");

        panel.add(deleteBtn);
        panel.add(blockBtn);
        panel.add(refreshBtn);

        add(panel, BorderLayout.SOUTH);

        // ACTIONS
        deleteBtn.addActionListener(e -> deleteFile());
        blockBtn.addActionListener(e -> blockUser());
        refreshBtn.addActionListener(e -> refreshTable());

        setVisible(true);
    }

    
    void loadData() {
        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT r.id, r.title, u.name, r.uploaded_by " +
                         "FROM resources r JOIN users u ON r.uploaded_by = u.id";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),  
                        rs.getInt(4)
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void refreshTable() {
        model.setRowCount(0);
        loadData();
    }

    
    void deleteFile() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a file");
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        try {
            Connection con = DBConnection.getConnection();

            
            PreparedStatement ps1 = con.prepareStatement(
                    "SELECT file_path FROM resources WHERE id=?"
            );
            ps1.setInt(1, id);
            ResultSet rs = ps1.executeQuery();

            String path = "";
            if (rs.next()) {
                path = rs.getString("file_path");
            }

            
            File f = new File(path);
            if (f.exists()) f.delete();

            
            PreparedStatement ps2 = con.prepareStatement(
                    "DELETE FROM resources WHERE id=?"
            );
            ps2.setInt(1, id);
            ps2.executeUpdate();

            JOptionPane.showMessageDialog(this, "File Deleted");
            refreshTable();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
    void blockUser() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user");
            return;
        }

        int userId = (int) model.getValueAt(row, 3);

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE users SET status='blocked' WHERE id=?"
            );
            ps.setInt(1, userId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "User Blocked");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}