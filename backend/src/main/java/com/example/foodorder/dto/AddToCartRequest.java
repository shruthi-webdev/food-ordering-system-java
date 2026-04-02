package com.example.foodorder.dto;

/**
 * Request body for POST /api/cart
 * Converted from a Java record to a plain class for pure-Java compatibility.
 */
public class AddToCartRequest {
    private String itemId;
    private int quantity;

    public AddToCartRequest() {}
    public AddToCartRequest(String itemId, int quantity) {
        this.itemId   = itemId;
        this.quantity = quantity;
    }

    public String itemId()   { return itemId; }
    public int    quantity() { return quantity; }
    public void setItemId(String itemId)     { this.itemId   = itemId; }
    public void setQuantity(int quantity)    { this.quantity = quantity; }
}
