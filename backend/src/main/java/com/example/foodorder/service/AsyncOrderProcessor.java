package com.example.foodorder.service;

import com.example.foodorder.model.Invoice;
import com.example.foodorder.model.Order;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Implements OrderProcessor by offloading billing to a background thread.
 *
 * OOP concepts:
 *   - Implements the OrderProcessor interface (polymorphism)
 *   - Encapsulates an ExecutorService (private thread pool)
 *   - Constructor injection: BillingService provided by caller (Main.java)
 *
 * Thread concept:
 *   - ExecutorService (single-thread pool) handles one order at a time
 *   - Future.get() blocks until billing completes — gives us async + result
 *   - shutdown() gracefully stops the thread pool
 */
public class AsyncOrderProcessor implements OrderProcessor {

    private final BillingService  billingService;

    /** Dedicated single-thread pool for billing operations. */
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "billing-thread");
        t.setDaemon(true); // dies when the main thread exits
        return t;
    });

    public AsyncOrderProcessor(BillingService billingService) {
        this.billingService = billingService;
    }

    /**
     * Submit billing to the thread pool and wait for the result.
     *
     * @param order         the placed order
     * @param paymentMethod e.g. "CARD", "UPI", "CASH"
     * @return computed Invoice
     */
    @Override
    public Invoice process(Order order, String paymentMethod) {
        Future<Invoice> future = executor.submit(
            () -> billingService.generateInvoice(order, paymentMethod)
        );

        try {
            return future.get(); // blocks until billing thread completes
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt(); // restore interrupt flag
            throw new IllegalStateException("Order processing was interrupted", ie);
        } catch (ExecutionException ee) {
            throw new IllegalStateException("Billing failed: " + ee.getCause().getMessage(), ee);
        }
    }

    /** Called by Main.java shutdown hook to release the thread pool. */
    public void shutdown() {
        executor.shutdown();
    }
}
