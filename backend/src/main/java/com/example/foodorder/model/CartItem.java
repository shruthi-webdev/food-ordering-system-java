package com.example.foodorder.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A single line item in the shopping cart.
 *
 * OOP concepts:
 *   - Implements Billable interface → can be totalled
 *   - Encapsulation: quantity is mutable via controlled methods only
 */
public class CartItem implements Billable {

    private final FoodItem foodItem;
    private int quantity;

    public CartItem(FoodItem foodItem, int quantity) {
        this.foodItem = Objects.requireNonNull(foodItem, "foodItem must not be null");
        this.quantity = Math.max(1, quantity);
    }

    public FoodItem getFoodItem() { return foodItem; }
    public int      getQuantity() { return quantity; }

    /** Increase quantity by amount (minimum 1). */
    public void increment(int amount) {
        quantity += Math.max(1, amount);
    }

    /** Set an explicit quantity (minimum 1). */
    public void setQuantity(int quantity) {
        this.quantity = Math.max(1, quantity);
    }

    /**
     * Billable implementation.
     * @return price × quantity
     */
    @Override
    public BigDecimal calculateTotal() {
        return foodItem.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
