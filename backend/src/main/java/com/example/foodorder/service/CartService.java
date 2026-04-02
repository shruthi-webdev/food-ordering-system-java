package com.example.foodorder.service;

import com.example.foodorder.model.CartItem;
import com.example.foodorder.model.FoodItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory shopping cart.
 *
 * OOP concepts:
 *   - Encapsulation: the cart map is private; only safe methods can mutate it
 *   - Dependency: receives MenuService via constructor injection (manual DI)
 *
 * Thread-safety:
 *   - ConcurrentHashMap for concurrent reads/writes
 *   - compute() for atomic add-or-increment operations
 */
public class CartService {

    private final MenuService menuService;

    /** Thread-safe map of itemId → CartItem */
    private final Map<String, CartItem> cart = new ConcurrentHashMap<>();

    public CartService(MenuService menuService) {
        this.menuService = menuService;
    }

    /** Return a snapshot of the current cart contents. */
    public List<CartItem> getItemsSnapshot() {
        return new ArrayList<>(cart.values());
    }

    /**
     * Add an item (or increment its quantity if already present).
     *
     * Thread concept: ConcurrentHashMap.compute() is an atomic operation —
     * no two threads can create or modify the same cart entry simultaneously.
     */
    public CartItem addItem(String itemId, int quantity) {
        FoodItem foodItem = menuService.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        return cart.compute(itemId, (key, existing) -> {
            if (existing == null) return new CartItem(foodItem, quantity);
            existing.increment(quantity);
            return existing;
        });
    }

    /** Remove an item from the cart. */
    public void removeItem(String itemId) {
        cart.remove(itemId);
    }

    /** Empty the entire cart (called after checkout). */
    public void clear() {
        cart.clear();
    }
}
