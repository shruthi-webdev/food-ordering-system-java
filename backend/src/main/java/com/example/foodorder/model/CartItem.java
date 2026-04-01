package com.example.foodorder.model;

import java.math.BigDecimal;
import java.util.Objects;

public class CartItem implements Billable {
    private final FoodItem foodItem;
    private int quantity;

    public CartItem(FoodItem foodItem, int quantity) {
        this.foodItem = Objects.requireNonNull(foodItem, "foodItem");
        this.quantity = Math.max(1, quantity);
    }

    public FoodItem getFoodItem() {
        return foodItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increment(int amount) {
        quantity += Math.max(1, amount);
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(1, quantity);
    }

    @Override
    public BigDecimal calculateTotal() {
        return foodItem.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
