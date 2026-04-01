package com.example.foodorder.service;

import com.example.foodorder.dto.ChatRecommendationResponse;
import com.example.foodorder.dto.MenuRecommendation;
import com.example.foodorder.dto.ParsedFoodIntent;
import com.example.foodorder.model.MenuItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Orchestrator: parses intent → gets menu → applies rules → builds response.
 */
@Service
public class RecommendationService {

    private final LlmIntentParserService intentParser;
    private final FoodRuleEngineService ruleEngine;
    // In production, inject a MenuItemService that provides List<MenuItem>
    // For now we use the extended menu data inline

    public RecommendationService(LlmIntentParserService intentParser,
                                  FoodRuleEngineService ruleEngine) {
        this.intentParser = intentParser;
        this.ruleEngine = ruleEngine;
    }

    public ChatRecommendationResponse getRecommendations(String userMessage) {
        // Step 1: Parse intent from user message
        ParsedFoodIntent intent = intentParser.parseIntent(userMessage);

        // Step 2: Get all menu items (replace with DB/service call in production)
        List<MenuItem> allItems = getMenuItems();

        // Step 3: Apply business rules
        List<MenuRecommendation> recommendations = ruleEngine.applyRules(intent, allItems);

        // Step 4: Build response message
        String message;
        if (intent.isContradictory()) {
            message = "⚠ " + intent.getClarificationNote();
        } else if (intent.isAmbiguous()) {
            message = "ℹ " + intent.getClarificationNote();
        } else if (recommendations.isEmpty()) {
            message = "No dishes match your exact criteria. Try broadening your request!";
        } else {
            message = "Found " + recommendations.size() + " dish"
                    + (recommendations.size() > 1 ? "es" : "") + " for you!";
        }

        return new ChatRecommendationResponse(intent, recommendations, message);
    }

    /**
     * Extended menu data with all fields needed for rule engine filtering.
     * In production, this would come from a database or MenuItemService.
     */
    private List<MenuItem> getMenuItems() {
        return List.of(
            new MenuItem("butter-chicken", "Butter Chicken",
                "Tender chicken in rich creamy tomato gravy",
                BigDecimal.valueOf(290), 4.8,
                "https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?auto=format&fit=crop&q=80&w=800",
                false, "MEDIUM", 20, true, 15, true, false, "DINNER", List.of("popular")),
            new MenuItem("paneer-tikka", "Paneer Tikka",
                "Cottage cheese marinated in yogurt and spices, grilled",
                BigDecimal.valueOf(220), 4.5,
                "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?auto=format&fit=crop&q=80&w=800",
                true, "SPICY", 15, true, 20, true, false, "DINNER", List.of("popular")),
            new MenuItem("biryani", "Hyderabadi Dum Biryani",
                "Fragrant basmati rice with marinated meat, slow-cooked",
                BigDecimal.valueOf(320), 4.9,
                "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&q=80&w=800",
                false, "SPICY", 35, true, 10, true, false, "LUNCH", List.of("chef-special")),
            new MenuItem("samosa", "Punjabi Samosas",
                "Golden crispy pastry filled with spiced potatoes and peas",
                BigDecimal.valueOf(110), 4.7,
                "https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&q=80&w=800",
                true, "MEDIUM", 10, true, 50, true, false, "SNACK", List.of("quick", "budget")),
            new MenuItem("naan", "Garlic Butter Naan",
                "Soft fluffy bread baked in tandoor, brushed with garlic butter",
                BigDecimal.valueOf(70), 4.6,
                "https://images.unsplash.com/photo-1626200271501-c85df8ca575f?auto=format&fit=crop&q=80&w=800",
                true, "MILD", 8, true, 100, true, false, "ANY", List.of("quick", "budget")),
            new MenuItem("palak-paneer", "Palak Paneer",
                "Soft paneer cubes in creamy spiced spinach sauce",
                BigDecimal.valueOf(250), 4.4,
                "https://images.unsplash.com/photo-1601050690117-94f5f6af3bb0?auto=format&fit=crop&q=80&w=800",
                true, "MILD", 18, true, 12, false, false, "LUNCH", List.of("healthy")),
            new MenuItem("masala-dosa", "Masala Dosa",
                "Crispy rice crepe stuffed with spiced potato filling",
                BigDecimal.valueOf(140), 4.6,
                "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?auto=format&fit=crop&q=80&w=800",
                true, "MEDIUM", 12, true, 25, true, false, "BREAKFAST", List.of("popular", "quick")),
            new MenuItem("chole-bhature", "Chole Bhature",
                "Spiced chickpea curry with deep-fried fluffy bread",
                BigDecimal.valueOf(170), 4.5,
                "https://images.unsplash.com/photo-1626132647523-66f5bf380027?auto=format&fit=crop&q=80&w=800",
                true, "SPICY", 20, true, 18, true, false, "LUNCH", List.of("popular")),
            new MenuItem("dal-makhani", "Dal Makhani",
                "Slow-cooked black lentils in buttery tomato cream sauce",
                BigDecimal.valueOf(200), 4.7,
                "https://images.unsplash.com/photo-1585937421612-70a008356fbe?auto=format&fit=crop&q=80&w=800",
                true, "MILD", 25, true, 15, true, false, "DINNER", List.of("comfort")),
            new MenuItem("chicken-tikka", "Chicken Tikka",
                "Juicy boneless chicken marinated and chargrilled in tandoor",
                BigDecimal.valueOf(260), 4.6,
                "https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?auto=format&fit=crop&q=80&w=800",
                false, "SPICY", 18, true, 20, true, false, "DINNER", List.of("popular"))
        );
    }
}
