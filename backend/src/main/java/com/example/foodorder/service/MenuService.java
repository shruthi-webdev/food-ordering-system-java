package com.example.foodorder.service;

import com.example.foodorder.model.FoodItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MenuService {
    private final Map<String, FoodItem> inventory = new LinkedHashMap<>();

    public MenuService() {
        inventory.put("butter-chicken", new FoodItem("butter-chicken", "Butter Chicken",
                "Tender chicken cooked in a rich, creamy tomato gravy with a hint of fenugreek",
                BigDecimal.valueOf(290), 4.8, "https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?auto=format&fit=crop&q=80&w=800"));
        inventory.put("paneer-tikka", new FoodItem("paneer-tikka", "Paneer Tikka",
                "Cottage cheese cubes marinated in yogurt and spices, grilled to perfection",
                BigDecimal.valueOf(220), 4.5, "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?auto=format&fit=crop&q=80&w=800"));
        inventory.put("biryani", new FoodItem("biryani", "Hyderabadi Dum Biryani",
                "Fragrant basmati rice layered with marinated meat and slow-cooked in a sealed pot",
                BigDecimal.valueOf(320), 4.9, "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&q=80&w=800"));
        inventory.put("samosa", new FoodItem("samosa", "Punjabi Samosas",
                "Golden, crispy pastry filled with spiced potatoes and peas, served with mint chutney",
                BigDecimal.valueOf(110), 4.7, "https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&q=80&w=800"));
        inventory.put("naan", new FoodItem("naan", "Garlic Butter Naan",
                "Soft, fluffy Indian bread baked in a tandoor, brushed with garlic butter",
                BigDecimal.valueOf(70), 4.6, "https://images.unsplash.com/photo-1626200271501-c85df8ca575f?auto=format&fit=crop&q=80&w=800"));
        inventory.put("palak-paneer", new FoodItem("palak-paneer", "Palak Paneer",
                "Soft paneer cubes simmered in a creamy, spiced spinach sauce",
                BigDecimal.valueOf(250), 4.4, "https://images.unsplash.com/photo-1601050690117-94f5f6af3bb0?auto=format&fit=crop&q=80&w=800"));
    }

    public List<FoodItem> findAll() {
        return new ArrayList<>(inventory.values());
    }

    public Optional<FoodItem> findById(String id) {
        return Optional.ofNullable(inventory.get(id));
    }
}
