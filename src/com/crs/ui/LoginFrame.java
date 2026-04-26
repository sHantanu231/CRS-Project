package com.crs.ui;

import javax.swing.*;

import com.crs.admin.AdminPanel;
import com.crs.db.DBConnection;

import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passField;

    public LoginFrame() {
        setTitle("CRS System - Login");
        setSize(420, 320);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(30, 100, 200));

        JLabel title = new JLabel("📚 College Resource Sharing", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        main.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 5, 6, 5);

        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Email:"), g);
        g.gridx = 1;
        emailField = new JTextField(18);
        form.add(emailField, g);

        g.gridx = 0; g.gridy = 1;
        form.add(new JLabel("Password:"), g);
        g.gridx = 1;
        passField = new JPasswordField(18);
        form.add(passField, g);

        g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(30, 100, 200));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        form.add(loginBtn, g);

        g.gridy = 3;
        JButton regBtn = new JButton("Don't have account? Register");
        regBtn.setBorderPainted(false);
        regBtn.setContentAreaFilled(false);
        regBtn.setForeground(new Color(30, 100, 200));
        regBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        form.add(regBtn, g);

        main.add(form, BorderLayout.CENTER);
        add(main);

        loginBtn.addActionListener(e -> doLogin());
        passField.addActionListener(e -> doLogin());
        regBtn.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });

        setVisible(true);
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String pass  = new String(passField.getPassword()).trim();

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int    userId   = rs.getInt("id");
                String userName = rs.getString("name");
                String role     = rs.getString("role");
                String status   = rs.getString("status");

              
                if (status != null && status.equalsIgnoreCase("blocked")) {
                    JOptionPane.showMessageDialog(this, "You are blocked!", "Access Denied", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(this, "Welcome, " + userName + "!");

               
                if (role != null && role.equalsIgnoreCase("admin")) {
                    new AdminPanel().setVisible(true);   
                } else {
                    new DashboardFrame(userId, userName, role).setVisible(true); 
                }

                dispose();

            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}