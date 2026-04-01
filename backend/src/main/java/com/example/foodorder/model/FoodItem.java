package com.example.foodorder.model;

import java.math.BigDecimal;
import java.util.Objects;

public class FoodItem {
    private final String id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final double rating;
    private final String imageUrl;

    public FoodItem(String id, String name, String description, BigDecimal price, double rating, String imageUrl) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.description = Objects.requireNonNull(description, "description");
        this.price = Objects.requireNonNull(price, "price");
        this.rating = rating;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public double getRating() {
        return rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
