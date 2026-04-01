package com.example.foodorder.service;

import com.example.foodorder.model.Invoice;
import com.example.foodorder.model.Order;

public interface OrderProcessor {
    Invoice process(Order order, String paymentMethod);
}
