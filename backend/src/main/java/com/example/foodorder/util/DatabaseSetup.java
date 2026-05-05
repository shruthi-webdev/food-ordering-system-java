package com.example.foodorder.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    public static void createTables() {
        String createFoodItems = "CREATE TABLE IF NOT EXISTS food_items (" +
                "id VARCHAR(255) PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "description TEXT, " +
                "price DECIMAL(10, 2) NOT NULL, " +
                "rating DOUBLE, " +
                "image_url VARCHAR(255));";

        String createOrders = "CREATE TABLE IF NOT EXISTS orders (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "customer_name VARCHAR(255) NOT NULL, " +
                "created_at TIMESTAMP NOT NULL);";

        String createOrderItems = "CREATE TABLE IF NOT EXISTS order_items (" +
                "order_id VARCHAR(36), " +
                "food_item_id VARCHAR(255), " +
                "quantity INT NOT NULL, " +
                "PRIMARY KEY (order_id, food_item_id), " +
                "FOREIGN KEY (order_id) REFERENCES orders(id), " +
                "FOREIGN KEY (food_item_id) REFERENCES food_items(id));";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createFoodItems);
            stmt.execute(createOrders);
            stmt.execute(createOrderItems);
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertInitialData() {
        String[] foodItems = {
            "('sushi-1', 'Sushi', 'Finest fish and rice', 10.99, 4.5, 'sushi.jpg')",
            "('pizza-1', 'Pizza', 'Cheesy and delicious', 12.99, 4.7, 'pizza.jpg')",
            "('burger-1', 'Burger', 'Juicy and savory', 8.99, 4.6, 'burger.jpg')"
        };

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            // Clear existing data
            stmt.execute("DELETE FROM order_items;");
            stmt.execute("DELETE FROM orders;");
            stmt.execute("DELETE FROM food_items;");

            System.out.println("Inserting initial food items...");
            for (String item : foodItems) {
                String sql = "INSERT INTO food_items (id, name, description, price, rating, image_url) VALUES " + item;
                stmt.executeUpdate(sql);
            }
            System.out.println("Initial data inserted.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        createTables();
        insertInitialData();
    }
}
