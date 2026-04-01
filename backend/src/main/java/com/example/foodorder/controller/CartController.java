package com.example.foodorder.controller;

import com.example.foodorder.dto.AddToCartRequest;
import com.example.foodorder.model.CartItem;
import com.example.foodorder.service.CartService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public List<CartItem> getCart() {
        return cartService.getItemsSnapshot();
    }

    @PostMapping
    public CartItem addToCart(@RequestBody AddToCartRequest request) {
        return cartService.addItem(request.itemId(), request.quantity());
    }

    @DeleteMapping("/{itemId}")
    public void removeFromCart(@PathVariable String itemId) {
        cartService.removeItem(itemId);
    }
}
