package com.example.foodorder.dto;

/**
 * Request body for POST /api/order/checkout
 */
public class CheckoutRequest {
    private String customerName;
    private String paymentMethod;

    public CheckoutRequest() {}
    public CheckoutRequest(String customerName, String paymentMethod) {
        this.customerName  = customerName;
        this.paymentMethod = paymentMethod;
    }

    public String customerName()  { return customerName; }
    public String paymentMethod() { return paymentMethod; }
    public void setCustomerName(String customerName)   { this.customerName  = customerName; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
