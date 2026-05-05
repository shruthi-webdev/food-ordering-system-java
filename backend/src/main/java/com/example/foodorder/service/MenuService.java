package com.example.foodorder.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.foodorder.model.FoodItem;
import com.example.foodorder.util.DatabaseUtil;

/**
 * In-memory catalogue of all menu items.
 *
 * OOP concepts:
 *   - Encapsulation: inventory map is private; exposed via controlled methods
 *   - Single Responsibility: only manages menu data, nothing else
 *
 * In production this would query a database; here it holds data in memory.
 */
public class MenuService {

    public List<FoodItem> findAll() {
        List<FoodItem> menu = new ArrayList<>();
        String sql = "SELECT * FROM food_items";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                menu.add(new FoodItem(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBigDecimal("price"),
                        rs.getDouble("rating"),
                        rs.getString("image_url")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menu;
    }
}
