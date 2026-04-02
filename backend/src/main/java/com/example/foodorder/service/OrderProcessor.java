package com.example.foodorder.service;

import com.example.foodorder.model.Invoice;
import com.example.foodorder.model.Order;

/**
 * Interface: OrderProcessor
 *
 * OOP concept: Interface (polymorphism)
 *   Defines the contract for processing an order.
 *   Anyone who calls process() does not need to know HOW it is processed
 *   (synchronously, asynchronously, with logging, etc.).
 *
 *   Implemented by:
 *     - AsyncOrderProcessor (submits to an ExecutorService thread pool)
 */
public interface OrderProcessor {

    /**
     * Process an order and produce a billing invoice.
     *
     * @param order         the order containing customer name and cart items
     * @param paymentMethod e.g. "CARD", "CASH", "UPI"
     * @return the generated Invoice
     */
    Invoice process(Order order, String paymentMethod);
}
