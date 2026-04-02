package com.example.foodorder.handler;

import com.example.foodorder.model.CartItem;
import com.example.foodorder.model.Invoice;
import com.example.foodorder.model.Order;
import com.example.foodorder.server.BaseHandler;
import com.example.foodorder.service.CartService;
import com.example.foodorder.service.OrderProcessor;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Order / checkout endpoint — replaces Spring's OrderController.
 *
 * Routes:
 *   POST /api/order/checkout  → validate cart, create Order, process via
 *                               AsyncOrderProcessor (thread!), clear cart,
 *                               return Invoice JSON
 *
 * Thread concept in action:
 *   orderProcessor.process() submits billing to a background thread via
 *   ExecutorService and blocks on Future.get() — demonstrating threads
 *   working behind an interface (OrderProcessor).
 */
public class OrderHandler extends BaseHandler {

    private final CartService    cartService;
    private final OrderProcessor orderProcessor;

    public OrderHandler(CartService cartService, OrderProcessor orderProcessor) {
        this.cartService    = cartService;
        this.orderProcessor = orderProcessor;
    }

    @Override
    protected void post(HttpExchange exchange) throws IOException {
        // The path could be /api/order/checkout — any POST is treated as checkout
        Map<String, String> body = parseJsonBody(exchange);

        List<CartItem> snapshot = new ArrayList<>(cartService.getItemsSnapshot());
        if (snapshot.isEmpty()) {
            sendError(exchange, 400, "Cart is empty");
            return;
        }

        String customerName  = body.getOrDefault("customerName",  "Guest");
        String paymentMethod = body.getOrDefault("paymentMethod", "CARD");

        // Build Order (implements Billable)
        Order order = new Order(customerName, snapshot);

        // Process via AsyncOrderProcessor (runs billing in a background thread)
        Invoice invoice = orderProcessor.process(order, paymentMethod);

        // Clear cart after successful checkout
        cartService.clear();

        sendJson(exchange, invoice);
    }
}
