package com.example.foodorder.service;

import com.example.foodorder.model.CartItem;
import com.example.foodorder.model.Invoice;
import com.example.foodorder.model.Order;
import com.example.foodorder.util.DatabaseUtil;
import com.example.foodorder.util.JsonUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

/**
 * Calculates billing totals and produces an Invoice.
 *
 * OOP concepts:
 *   - Single Responsibility: only knows how to generate invoices
 *   - Encapsulation: TAX_RATE is a private constant
 */
public class BillingService {

    private static final BigDecimal TAX_RATE = BigDecimal.valueOf(0.07); // 7%

    /**
     * Generate an invoice for the given order.
     *
     * @param order         the order with all cart items
     * @param paymentMethod payment type string
     * @return fully computed Invoice
     */
    public Invoice generateInvoice(Order order, String paymentMethod) {
        List<CartItem> snapshot = order.getItems();

        BigDecimal subTotal = order.calculateTotal()
                                   .setScale(2, RoundingMode.HALF_UP);
        BigDecimal tax      = subTotal.multiply(TAX_RATE)
                                      .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total    = subTotal.add(tax);

        String method = (paymentMethod == null || paymentMethod.isBlank())
                        ? "CARD" : paymentMethod.toUpperCase();

        Invoice invoice = new Invoice(order.getId(), Instant.now(), snapshot,
                subTotal, tax, total, method);

        saveInvoiceToDatabase(invoice);

        return invoice;
    }

    private void saveInvoiceToDatabase(Invoice invoice) {
        String sql = "INSERT INTO foodsales (customerName, totalAmount, invoiceDetails) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "Customer"); // You might want to get the actual customer name
            pstmt.setDouble(2, invoice.getTotal().doubleValue());
            pstmt.setString(3, JsonUtil.toJson(invoice));

            pstmt.executeUpdate();
            System.out.println("Invoice saved to database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
