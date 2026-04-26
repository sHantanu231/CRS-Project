package com.crs.ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.sql.*;

import com.crs.db.DBConnection;
import com.crs.utils.PlagiarismChecker;
import com.crs.utils.ContentValidator;

public class UploadFrame extends JFrame {

    private JTextField titleField, subjectField, filePathField;
    private JTextArea descArea;
    private JComboBox<String> semBox;
    private int userId;

    public UploadFrame(int userId, String userName, String role) {
        this.userId = userId;

        setTitle("Upload Resource");
        setSize(500, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 5, 8, 5);

        // Title
        g.gridx = 0; g.gridy = 0;
        panel.add(new JLabel("Title:"), g);
        g.gridx = 1;
        titleField = new JTextField();
        panel.add(titleField, g);

        // Subject
        g.gridx = 0; g.gridy++;
        panel.add(new JLabel("Subject:"), g);
        g.gridx = 1;
        subjectField = new JTextField();
        panel.add(subjectField, g);

        // Semester
        g.gridx = 0; g.gridy++;
        panel.add(new JLabel("Semester:"), g);
        g.gridx = 1;
        semBox = new JComboBox<>(new String[]{"1","2","3","4","5","6","7","8"});
        panel.add(semBox, g);

        // Description
        g.gridx = 0; g.gridy++;
        panel.add(new JLabel("Description:"), g);
        g.gridx = 1;
        descArea = new JTextArea(3, 20);
        panel.add(new JScrollPane(descArea), g);

        // File path
        g.gridx = 0; g.gridy++;
        panel.add(new JLabel("File:"), g);
        g.gridx = 1;
        filePathField = new JTextField();
        panel.add(filePathField, g);

        JButton browseBtn = new JButton("Browse");
        browseBtn.addActionListener(e -> chooseFile());
        g.gridx = 2;
        panel.add(browseBtn, g);

        // Upload button
        JButton uploadBtn = new JButton("Upload");
        uploadBtn.addActionListener(e -> doUpload());
        g.gridx = 1; g.gridy++;
        panel.add(uploadBtn, g);

        add(panel);
        setVisible(true);
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            filePathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void doUpload() {

        String title = titleField.getText().trim();
        String subject = subjectField.getText().trim();
        int semester = Integer.parseInt((String) semBox.getSelectedItem());
        String desc = descArea.getText().trim();
        String srcPath = filePathField.getText().trim();

        if (title.isEmpty() || subject.isEmpty() || srcPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields!");
            return;
        }

        try {
            // ✅ VALIDATION
            if (!ContentValidator.isValid(title, subject, srcPath)) {
                JOptionPane.showMessageDialog(this, "Invalid file or content!");
                return;
            }

            // 🔐 GENERATE HASH
            String hash = PlagiarismChecker.getFileHash(srcPath);

            if (hash == null) {
                JOptionPane.showMessageDialog(this, "Error generating file hash!");
                return;
            }

            Connection con = DBConnection.getConnection();

            // 🔁 DUPLICATE CHECK
            PreparedStatement check = con.prepareStatement(
                "SELECT id FROM resources WHERE file_hash=?"
            );
            check.setString(1, hash);

            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Duplicate file detected!");
                return;
            }

            // 📂 COPY FILE
            File src = new File(srcPath);
            new File("uploads").mkdirs();

            String savedName = System.currentTimeMillis() + "_" + src.getName();
            Path dest = Paths.get("uploads", savedName);

            Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

            // 💾 INSERT INTO DB (FIXED)
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO resources (title, description, semester, subject, file_name, file_path, uploaded_by, file_hash) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );

            ps.setString(1, title);
            ps.setString(2, desc);
            ps.setInt(3, semester);
            ps.setString(4, subject);
            ps.setString(5, savedName);
            ps.setString(6, dest.toString());
            ps.setInt(7, userId);
            ps.setString(8, hash); // 🔥 IMPORTANT FIX

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Uploaded successfully!");
            dispose();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}