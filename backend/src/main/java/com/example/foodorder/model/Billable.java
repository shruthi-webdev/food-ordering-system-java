package com.example.foodorder.model;

import java.math.BigDecimal;

public interface Billable {
    BigDecimal calculateTotal();
}
