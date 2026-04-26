package com.crs.ui;

import javax.swing.*;

import com.crs.db.DBConnection;

import java.awt.*;
import java.sql.*;

public class RegisterFrame extends JFrame {

    private JTextField nameField, emailField;
    private JPasswordField passField, confirmField;

    public RegisterFrame() {
        setTitle("CRS System - Register");
        setSize(420, 370);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(7, 5, 7, 5);

        JLabel heading = new JLabel("Create Account", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        form.add(heading, g);

        g.gridwidth = 1;

      
        g.gridx = 0; g.gridy = 1; form.add(new JLabel("Full Name:"), g);
        g.gridx = 1; nameField = new JTextField(18); form.add(nameField, g);

        g.gridx = 0; g.gridy = 2; form.add(new JLabel("Email:"), g);
        g.gridx = 1; emailField = new JTextField(18); form.add(emailField, g);

        
        g.gridx = 0; g.gridy = 3; form.add(new JLabel("Password:"), g);
        g.gridx = 1; passField = new JPasswordField(18); form.add(passField, g);

       
        g.gridx = 0; g.gridy = 4; form.add(new JLabel("Confirm Password:"), g);
        g.gridx = 1; confirmField = new JPasswordField(18); form.add(confirmField, g);

        
        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        JButton regBtn = new JButton("Register");
        regBtn.setBackground(new Color(30, 100, 200));
        regBtn.setForeground(Color.WHITE);
        regBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        form.add(regBtn, g);

        
        g.gridy = 6;
        JButton backBtn = new JButton("Already have account? Login");
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setForeground(new Color(30, 100, 200));
        form.add(backBtn, g);

        add(form);

        regBtn.addActionListener(e -> doRegister());
        backBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        setVisible(true);
    }

    private void doRegister() {
        String name    = nameField.getText().trim();
        String email   = emailField.getText().trim();
        String pass    = new String(passField.getPassword()).trim();
        String confirm = new String(confirmField.getPassword()).trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords don't match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            
            PreparedStatement check = con.prepareStatement("SELECT id FROM users WHERE email=?");
            check.setString(1, email);
            if (check.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Email already registered!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users (name, email, password, role) VALUES (?,?,?,'student')");
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, pass);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registered successfully! Please login.");
            new LoginFrame();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}