package com.crs.db;

import java.sql.Connection;
import java.sql.DriverManager;


public class DBConnection {
    private static final String URL  = "jdbc:mysql://localhost:3306/college_resources?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "linkinpark2006"; 
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String[] args) {
        Connection con = getConnection();

        if (con != null) {
            System.out.println("✅ Connected Successfully!");
        } else {
            System.out.println("❌ Connection Failed!");
        }
    }
}