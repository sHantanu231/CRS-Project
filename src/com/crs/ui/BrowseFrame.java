package com.crs.ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;

import com.crs.db.DBConnection;
import com.crs.ui.RatingFrame;

public class BrowseFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> semFilter;
    private int userId;

    public BrowseFrame(int userId, String userName, String role, int defaultSem) {
        this.userId = userId;

        setTitle("Browse Resources");
        setSize(850, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // 🔍 TOP PANEL (Search + Filter)
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        top.add(new JLabel("Search:"));
        searchField = new JTextField(15);
        top.add(searchField);

        top.add(new JLabel("Semester:"));
        String[] sems = {"All","1","2","3","4","5","6","7","8"};
        semFilter = new JComboBox<>(sems);
        if (defaultSem > 0)
            semFilter.setSelectedItem(String.valueOf(defaultSem));
        top.add(semFilter);

        JButton searchBtn = new JButton("🔍 Search");
        searchBtn.addActionListener(e -> loadData());
        top.add(searchBtn);

        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            semFilter.setSelectedIndex(0);
            loadData();
        });
        top.add(clearBtn);

        add(top, BorderLayout.NORTH);

        // 📊 TABLE
        String[] cols = {"ID","Title","Semester","Subject","Uploaded By","Date","Rating","Downloads"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        add(new JScrollPane(table), BorderLayout.CENTER);

        // 🔘 BOTTOM PANEL
        JPanel bottom = new JPanel();

        JButton openBtn = new JButton("⬇ Open / Download");
        JButton rateBtn = new JButton("⭐ Rate");

        bottom.add(openBtn);
        bottom.add(rateBtn);

        add(bottom, BorderLayout.SOUTH);

        // ACTIONS
        openBtn.addActionListener(e -> openSelected());

        rateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int resourceId = (int) model.getValueAt(row, 0);
                new RatingFrame(userId, resourceId);
            } else {
                JOptionPane.showMessageDialog(this, "Select a resource first!");
            }
        });

        // Double click to open
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)
                    openSelected();
            }
        });

        loadData();
        setVisible(true);
    }

    // 🔄 LOAD DATA
    private void loadData() {

        model.setRowCount(0);

        String keyword = searchField.getText().trim();
        String sem = (String) semFilter.getSelectedItem();

        StringBuilder sql = new StringBuilder(
            "SELECT r.id, r.title, r.semester, r.subject, u.name, r.upload_date, r.avg_rating, r.download_count " +
            "FROM resources r LEFT JOIN users u ON r.uploaded_by = u.id WHERE 1=1"
        );

        if (!keyword.isEmpty()) {
            sql.append(" AND (r.title LIKE '%").append(keyword)
               .append("%' OR r.subject LIKE '%").append(keyword).append("%')");
        }

        if (!sem.equals("All")) {
            sql.append(" AND r.semester=").append(sem);
        }

        sql.append(" ORDER BY r.avg_rating DESC, r.download_count DESC");

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql.toString())) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        "Sem " + rs.getInt("semester"),
                        rs.getString("subject"),
                        rs.getString("name"),
                        rs.getString("upload_date"),
                        rs.getFloat("avg_rating"),
                        rs.getInt("download_count")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    // 📂 OPEN / DOWNLOAD FILE
    private void openSelected() {

        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a resource!");
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        try (Connection con = DBConnection.getConnection()) {

            // Get file path
            PreparedStatement ps = con.prepareStatement(
                "SELECT file_path FROM resources WHERE id=?"
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                File file = new File(rs.getString("file_path"));

                if (file.exists()) {

                    Desktop.getDesktop().open(file);

                    // 🔥 UPDATE DOWNLOAD COUNT
                    PreparedStatement ps2 = con.prepareStatement(
                        "UPDATE resources SET download_count = download_count + 1 WHERE id=?"
                    );
                    ps2.setInt(1, id);
                    ps2.executeUpdate();

                } else {
                    JOptionPane.showMessageDialog(this, "File not found!");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}