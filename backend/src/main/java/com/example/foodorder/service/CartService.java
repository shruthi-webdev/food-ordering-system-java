package com.example.foodorder.service;

import com.example.foodorder.model.CartItem;
import com.example.foodorder.model.FoodItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CartService {
    private final MenuService menuService;
    private final Map<String, CartItem> cart = new ConcurrentHashMap<>();

    public CartService(MenuService menuService) {
        this.menuService = menuService;
    }

    public List<CartItem> getItemsSnapshot() {
        return new ArrayList<>(cart.values());
    }

    public CartItem addItem(String itemId, int quantity) {
        FoodItem foodItem = menuService.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        return cart.compute(itemId, (key, existing) -> {
            if (existing == null) {
                return new CartItem(foodItem, quantity);
            }
            existing.increment(quantity);
            return existing;
        });
    }

    public void removeItem(String itemId) {
        cart.remove(itemId);
    }

    public void clear() {
        cart.clear();
    }
}
