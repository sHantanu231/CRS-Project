package com.crs.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import com.crs.db.DBConnection;

public class RatingFrame extends JFrame {

    private int userId, resourceId;

    public RatingFrame(int userId, int resourceId) {
        this.userId = userId;
        this.resourceId = resourceId;

        setTitle("Rate Resource");
        setSize(300, 180);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3,1,10,10));

        JLabel label = new JLabel("Rate (1-5):", SwingConstants.CENTER);

        Integer[] stars = {1,2,3,4,5};
        JComboBox<Integer> ratingBox = new JComboBox<>(stars);

        JButton submitBtn = new JButton("Submit");

        submitBtn.addActionListener(e -> saveRating((int)ratingBox.getSelectedItem()));

        panel.add(label);
        panel.add(ratingBox);
        panel.add(submitBtn);

        add(panel);
        setVisible(true);
    }

    private void saveRating(int rating) {
        try (Connection con = DBConnection.getConnection()) {

            String sql = "INSERT INTO ratings (user_id, resource_id, rating) VALUES (?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE rating=?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, resourceId);
            ps.setInt(3, rating);
            ps.setInt(4, rating);
            ps.executeUpdate();

            // update avg rating
            PreparedStatement ps2 = con.prepareStatement(
                "UPDATE resources SET avg_rating = " +
                "(SELECT AVG(rating) FROM ratings WHERE resource_id=?) WHERE id=?");

            ps2.setInt(1, resourceId);
            ps2.setInt(2, resourceId);
            ps2.executeUpdate();

            JOptionPane.showMessageDialog(this, "Rating saved!");
            dispose();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
