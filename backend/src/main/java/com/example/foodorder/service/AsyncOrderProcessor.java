package com.example.foodorder.service;

import com.example.foodorder.model.Invoice;
import com.example.foodorder.model.Order;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class AsyncOrderProcessor implements OrderProcessor {
    private final BillingService billingService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public AsyncOrderProcessor(BillingService billingService) {
        this.billingService = billingService;
    }

    @Override
    public Invoice process(Order order, String paymentMethod) {
        Future<Invoice> future = executorService.submit(() -> billingService.generateInvoice(order, paymentMethod));
        try {
            return future.get();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Order processing interrupted", ie);
        } catch (ExecutionException ee) {
            throw new IllegalStateException("Failed to complete order", ee);
        }
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }
}
