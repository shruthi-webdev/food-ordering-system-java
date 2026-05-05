package com.example.foodorder.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    public static void createTables() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS foodsales ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "customerName VARCHAR(255) NOT NULL,"
                + "totalAmount DOUBLE NOT NULL,"
                + "invoiceDetails TEXT NOT NULL,"
                + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Table 'foodsales' created or already exists.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
