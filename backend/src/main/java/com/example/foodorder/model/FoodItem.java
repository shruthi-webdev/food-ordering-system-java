package com.example.foodorder.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A basic menu item shown on the MenuBoard.
 *
 * OOP concepts:
 *   - Encapsulation: all fields are private; accessed only via public getters
 *   - Immutability: all fields are final; the object cannot change after construction
 */
public class FoodItem {

    private final String id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final double rating;
    private final String imageUrl;

    public FoodItem(String id, String name, String description,
                    BigDecimal price, double rating, String imageUrl) {
        this.id          = Objects.requireNonNull(id,          "id must not be null");
        this.name        = Objects.requireNonNull(name,        "name must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.price       = Objects.requireNonNull(price,       "price must not be null");
        this.rating      = rating;
        this.imageUrl    = imageUrl;
    }

    public String     getId()          { return id; }
    public String     getName()        { return name; }
    public String     getDescription() { return description; }
    public BigDecimal getPrice()       { return price; }
    public double     getRating()      { return rating; }
    public String     getImageUrl()    { return imageUrl; }
}
