package com.example.foodorder.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Represents a customer's placed order (snapshot of cart items).
 *
 * OOP concepts:
 *   - Implements Billable interface → provides calculateTotal()
 *   - Immutability: id, customerName, and createdAt are final
 *   - Defensive copy: items list is copied in constructor and returned unmodifiable
 */
public class Order implements Billable {

    private final String      id;
    private final String      customerName;
    private final Instant     createdAt;
    private final List<CartItem> items;

    public Order(String customerName, List<CartItem> items) {
        this.id           = UUID.randomUUID().toString();
        this.customerName = (customerName == null || customerName.isBlank())
                            ? "Guest" : customerName.trim();
        this.createdAt    = Instant.now();
        this.items        = new ArrayList<>(items); // defensive copy
    }

    public String         getId()           { return id; }
    public String         getCustomerName() { return customerName; }
    public Instant        getCreatedAt()    { return createdAt; }
    public List<CartItem> getItems()        { return Collections.unmodifiableList(items); }

    /**
     * Billable implementation.
     * @return sum of all CartItem totals
     */
    @Override
    public BigDecimal calculateTotal() {
        return items.stream()
                    .map(CartItem::calculateTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
