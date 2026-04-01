package com.example.foodorder.service;

import com.example.foodorder.model.CartItem;
import com.example.foodorder.model.Invoice;
import com.example.foodorder.model.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
public class BillingService {
    private static final BigDecimal TAX_RATE = BigDecimal.valueOf(0.07);

    public Invoice generateInvoice(Order order, String paymentMethod) {
        List<CartItem> snapshot = order.getItems();
        BigDecimal subTotal = order.calculateTotal().setScale(2, RoundingMode.HALF_UP);
        BigDecimal tax = subTotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subTotal.add(tax);

        return new Invoice(
                order.getId(),
                Instant.now(),
                snapshot,
                subTotal,
                tax,
                total,
                paymentMethod == null || paymentMethod.isBlank() ? "CARD" : paymentMethod.toUpperCase()
        );
    }
}
