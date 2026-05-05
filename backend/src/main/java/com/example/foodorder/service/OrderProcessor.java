package com.example.foodorder.service;

import com.example.foodorder.model.CartItem;
import com.example.foodorder.model.Invoice;
import com.example.foodorder.model.Order;
import com.example.foodorder.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public interface OrderProcessor {
    Invoice process(Order order, String paymentMethod);
}

class DatabaseOrderProcessor implements OrderProcessor {

    private final BillingService billingService;

    public DatabaseOrderProcessor(BillingService billingService) {
        this.billingService = billingService;
    }

    @Override
    public Invoice process(Order order, String paymentMethod) {
        saveOrder(order);
        return billingService.bill(order, paymentMethod);
    }

    private void saveOrder(Order order) {
        String orderSql = "INSERT INTO orders (id, customer_name, created_at) VALUES (?, ?, ?)";
        String orderItemSql = "INSERT INTO order_items (order_id, food_item_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement orderPstmt = conn.prepareStatement(orderSql)) {
                orderPstmt.setString(1, order.getId());
                orderPstmt.setString(2, order.getCustomerName());
                orderPstmt.setTimestamp(3, Timestamp.from(order.getCreatedAt()));
                orderPstmt.executeUpdate();
            }

            try (PreparedStatement orderItemPstmt = conn.prepareStatement(orderItemSql)) {
                for (CartItem item : order.getItems()) {
                    orderItemPstmt.setString(1, order.getId());
                    orderItemPstmt.setString(2, item.getFoodItem().getId());
                    orderItemPstmt.setInt(3, item.getQuantity());
                    orderItemPstmt.addBatch();
                }
                orderItemPstmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider rolling back transaction
        }
    }
}
