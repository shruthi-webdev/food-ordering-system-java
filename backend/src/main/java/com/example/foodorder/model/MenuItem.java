package com.example.foodorder.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Extended menu item with all fields needed for the smart food assistant.
 * This extends the original FoodItem with dietary, prep, stock, and meal metadata.
 */
public class MenuItem {
    private final String id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final double rating;
    private final String imageUrl;

    // New fields for chat assistant
    private final boolean veg;
    private final String spiceLevel;       // MILD, MEDIUM, SPICY, EXTRA_SPICY
    private final int prepTimeMinutes;
    private final boolean available;
    private final int stockCount;
    private final boolean containsOnion;
    private final boolean containsNuts;
    private final String mealType;         // BREAKFAST, LUNCH, DINNER, SNACK, ANY
    private final List<String> tags;       // e.g. ["quick", "popular", "chef-special"]

    public MenuItem(String id, String name, String description, BigDecimal price,
                    double rating, String imageUrl, boolean veg, String spiceLevel,
                    int prepTimeMinutes, boolean available, int stockCount,
                    boolean containsOnion, boolean containsNuts, String mealType,
                    List<String> tags) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.price = Objects.requireNonNull(price);
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.veg = veg;
        this.spiceLevel = spiceLevel;
        this.prepTimeMinutes = prepTimeMinutes;
        this.available = available;
        this.stockCount = stockCount;
        this.containsOnion = containsOnion;
        this.containsNuts = containsNuts;
        this.mealType = mealType;
        this.tags = tags != null ? tags : List.of();
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public double getRating() { return rating; }
    public String getImageUrl() { return imageUrl; }
    public boolean isVeg() { return veg; }
    public String getSpiceLevel() { return spiceLevel; }
    public int getPrepTimeMinutes() { return prepTimeMinutes; }
    public boolean isAvailable() { return available; }
    public int getStockCount() { return stockCount; }
    public boolean isContainsOnion() { return containsOnion; }
    public boolean isContainsNuts() { return containsNuts; }
    public String getMealType() { return mealType; }
    public List<String> getTags() { return tags; }
}
