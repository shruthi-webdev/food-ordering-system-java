package com.example.foodorder.service;

import com.example.foodorder.model.FoodItem;
import com.example.foodorder.model.MenuItem;

import java.math.BigDecimal;
import java.util.*;

/**
 * In-memory catalogue of all menu items.
 *
 * OOP concepts:
 *   - Encapsulation: inventory map is private; exposed via controlled methods
 *   - Single Responsibility: only manages menu data, nothing else
 *
 * In production this would query a database; here it holds data in memory.
 */
public class MenuService {

    // ── Basic FoodItem catalogue (for MenuBoard) ───────────────────────────
    private final Map<String, FoodItem> inventory = new LinkedHashMap<>();

    // ── Extended MenuItem catalogue (for Chat Assistant rule engine) ────────
    private final List<MenuItem> extendedMenu = new ArrayList<>();

    public MenuService() {
        buildInventory();
        buildExtendedMenu();
    }

    // ── Public API ──────────────────────────────────────────────────────────

    public List<FoodItem> findAll() {
        return new ArrayList<>(inventory.values());
    }

    public Optional<FoodItem> findById(String id) {
        return Optional.ofNullable(inventory.get(id));
    }

    public List<MenuItem> findAllExtended() {
        return Collections.unmodifiableList(extendedMenu);
    }

    // ── Data initialisation ─────────────────────────────────────────────────

    private void buildInventory() {
        add("butter-chicken",  "Butter Chicken",
            "Tender chicken cooked in a rich, creamy tomato gravy with a hint of fenugreek",
            290, 4.8, "https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?auto=format&fit=crop&q=80&w=800");
        add("paneer-tikka",    "Paneer Tikka",
            "Cottage cheese cubes marinated in yogurt and spices, grilled to perfection",
            220, 4.5, "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?auto=format&fit=crop&q=80&w=800");
        add("biryani",         "Hyderabadi Dum Biryani",
            "Fragrant basmati rice layered with marinated meat and slow-cooked in a sealed pot",
            320, 4.9, "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&q=80&w=800");
        add("samosa",          "Punjabi Samosas",
            "Golden, crispy pastry filled with spiced potatoes and peas, served with mint chutney",
            110, 4.7, "https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&q=80&w=800");
        add("naan",            "Garlic Butter Naan",
            "Soft, fluffy Indian bread baked in a tandoor, brushed with garlic butter",
            70,  4.6, "https://images.unsplash.com/photo-1626200271501-c85df8ca575f?auto=format&fit=crop&q=80&w=800");
        add("palak-paneer",    "Palak Paneer",
            "Soft paneer cubes simmered in a creamy, spiced spinach sauce",
            250, 4.4, "https://images.unsplash.com/photo-1601050690117-94f5f6af3bb0?auto=format&fit=crop&q=80&w=800");
        add("masala-dosa",     "Masala Dosa",
            "Crispy rice crepe stuffed with spiced potato filling",
            140, 4.6, "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?auto=format&fit=crop&q=80&w=800");
        add("chole-bhature",   "Chole Bhature",
            "Spiced chickpea curry with deep-fried fluffy bread",
            170, 4.5, "https://images.unsplash.com/photo-1626132647523-66f5bf380027?auto=format&fit=crop&q=80&w=800");
        add("dal-makhani",     "Dal Makhani",
            "Slow-cooked black lentils in buttery tomato cream sauce",
            200, 4.7, "https://images.unsplash.com/photo-1585937421612-70a008356fbe?auto=format&fit=crop&q=80&w=800");
        add("chicken-tikka",   "Chicken Tikka",
            "Juicy boneless chicken marinated and chargrilled in tandoor",
            260, 4.6, "https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?auto=format&fit=crop&q=80&w=800");
    }

    private void add(String id, String name, String desc, int price, double rating, String img) {
        inventory.put(id, new FoodItem(id, name, desc, BigDecimal.valueOf(price), rating, img));
    }

    private void buildExtendedMenu() {
        extendedMenu.add(mi("butter-chicken", false, "MEDIUM",  20, 15, true,  false, "DINNER",    "popular"));
        extendedMenu.add(mi("paneer-tikka",   true,  "SPICY",   15, 20, true,  false, "DINNER",    "popular"));
        extendedMenu.add(mi("biryani",        false, "SPICY",   35, 10, true,  false, "LUNCH",     "chef-special"));
        extendedMenu.add(mi("samosa",         true,  "MEDIUM",  10, 50, true,  false, "SNACK",     "quick", "budget"));
        extendedMenu.add(mi("naan",           true,  "MILD",    8,  100,false, false, "ANY",       "quick", "budget"));
        extendedMenu.add(mi("palak-paneer",   true,  "MILD",    18, 12, false, false, "LUNCH",     "healthy"));
        extendedMenu.add(mi("masala-dosa",    true,  "MEDIUM",  12, 25, true,  false, "BREAKFAST", "popular", "quick"));
        extendedMenu.add(mi("chole-bhature",  true,  "SPICY",   20, 18, true,  false, "LUNCH",     "popular"));
        extendedMenu.add(mi("dal-makhani",    true,  "MILD",    25, 15, true,  false, "DINNER",    "comfort"));
        extendedMenu.add(mi("chicken-tikka",  false, "SPICY",   18, 20, true,  false, "DINNER",    "popular"));
    }

    /** Helper: build MenuItem using data already stored in inventory. */
    private MenuItem mi(String id, boolean veg, String spice, int prep, int stock,
                        boolean hasOnion, boolean hasNuts, String meal, String... tags) {
        FoodItem fi = inventory.get(id);
        return new MenuItem(
            fi.getId(), fi.getName(), fi.getDescription(), fi.getPrice(), fi.getRating(), fi.getImageUrl(),
            veg, spice, prep, true, stock, hasOnion, hasNuts, meal, Arrays.asList(tags)
        );
    }
}
