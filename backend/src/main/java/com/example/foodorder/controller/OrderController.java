package com.example.foodorder.controller;

import com.example.foodorder.dto.CheckoutRequest;
import com.example.foodorder.model.CartItem;
import com.example.foodorder.model.Invoice;
import com.example.foodorder.model.Order;
import com.example.foodorder.service.CartService;
import com.example.foodorder.service.OrderProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/order")
public class OrderController {
    private final CartService cartService;
    private final OrderProcessor orderProcessor;

    public OrderController(CartService cartService, OrderProcessor orderProcessor) {
        this.cartService = cartService;
        this.orderProcessor = orderProcessor;
    }

    @PostMapping("/checkout")
    public Invoice checkout(@RequestBody CheckoutRequest request) {
        List<CartItem> snapshot = new ArrayList<>(cartService.getItemsSnapshot());
        if (snapshot.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        Order order = new Order(request.customerName(), snapshot);
        Invoice invoice = orderProcessor.process(order, request.paymentMethod());
        cartService.clear();
        return invoice;
    }
}
