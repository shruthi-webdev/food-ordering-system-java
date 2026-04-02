package com.example.foodorder.handler;

import com.example.foodorder.model.CartItem;
import com.example.foodorder.server.BaseHandler;
import com.example.foodorder.service.CartService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

/**
 * Cart endpoint handler — replaces Spring's CartController.
 *
 * Routes (all under /api/cart):
 *   GET    /api/cart          → return all items
 *   POST   /api/cart          → add item (body: {"itemId":"...", "quantity":N})
 *   DELETE /api/cart/{itemId} → remove item
 *
 * Extends BaseHandler → inherits CORS, verb dispatch, JSON helpers.
 */
public class CartHandler extends BaseHandler {

    private final CartService cartService;

    public CartHandler(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        sendJson(exchange, cartService.getItemsSnapshot());
    }

    @Override
    protected void post(HttpExchange exchange) throws IOException {
        Map<String, String> body = parseJsonBody(exchange);

        String itemId = body.get("itemId");
        if (itemId == null || itemId.isBlank()) {
            sendError(exchange, 400, "itemId is required");
            return;
        }

        int quantity = 1;
        try {
            String qtyStr = body.get("quantity");
            if (qtyStr != null) quantity = Integer.parseInt(qtyStr);
        } catch (NumberFormatException e) {
            sendError(exchange, 400, "quantity must be a number");
            return;
        }

        CartItem added = cartService.addItem(itemId, quantity);
        sendJson(exchange, 200, added);
    }

    @Override
    protected void delete(HttpExchange exchange) throws IOException {
        // URL pattern: /api/cart/{itemId}
        String itemId = lastPathSegment(exchange);
        if (itemId.isBlank() || itemId.equals("cart")) {
            sendError(exchange, 400, "itemId is required in path");
            return;
        }
        cartService.removeItem(itemId);
        sendEmpty(exchange, 204);
    }
}
