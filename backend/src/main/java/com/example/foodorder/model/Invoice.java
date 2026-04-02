package com.example.foodorder.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Immutable invoice generated after a successful checkout.
 *
 * OOP concept: Encapsulation — all fields are private and final.
 * The invoice is created once by BillingService and never modified.
 */
public class Invoice {

    private final String      orderId;
    private final Instant     issuedAt;
    private final List<CartItem> lineItems;
    private final BigDecimal  subTotal;
    private final BigDecimal  tax;
    private final BigDecimal  total;
    private final String      paymentMethod;

    public Invoice(String orderId, Instant issuedAt, List<CartItem> lineItems,
                   BigDecimal subTotal, BigDecimal tax, BigDecimal total,
                   String paymentMethod) {
        this.orderId       = orderId;
        this.issuedAt      = issuedAt;
        this.lineItems     = lineItems;
        this.subTotal      = subTotal;
        this.tax           = tax;
        this.total         = total;
        this.paymentMethod = paymentMethod;
    }

    public String         getOrderId()       { return orderId; }
    public Instant        getIssuedAt()      { return issuedAt; }
    public List<CartItem> getLineItems()     { return lineItems; }
    public BigDecimal     getSubTotal()      { return subTotal; }
    public BigDecimal     getTax()           { return tax; }
    public BigDecimal     getTotal()         { return total; }
    public String         getPaymentMethod() { return paymentMethod; }
}
