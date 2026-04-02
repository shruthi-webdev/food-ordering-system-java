package com.example.foodorder.service;

import com.example.foodorder.dto.MenuRecommendation;
import com.example.foodorder.dto.ParsedFoodIntent;
import com.example.foodorder.model.MenuItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business rule engine for food recommendations.
 *
 * All dish selection logic lives here — the LLM only produces the intent,
 * the rule engine decides which dishes match.
 *
 * OOP concepts:
 *   - Single Responsibility: only applies rules, never parses or formats
 *   - Pure functions: stateless — same input always yields same output
 */
public class FoodRuleEngineService {

    /**
     * Filter and rank menu items against the parsed user intent.
     *
     * @param intent    structured intent from the LLM parser
     * @param menuItems full catalogue of available items
     * @return sorted list of top-N recommendations with scores and reasons
     */
    public List<MenuRecommendation> applyRules(ParsedFoodIntent intent, List<MenuItem> menuItems) {
        List<MenuRecommendation> results = new ArrayList<>();

        for (MenuItem item : menuItems) {

            // ── Hard filters (must ALL pass) ────────────────────────────────

            if (!item.isAvailable() || item.getStockCount() <= 0) continue;

            if (intent.getVeg() != null) {
                if ( intent.getVeg() && !item.isVeg())  continue; // veg wanted → skip non-veg
                if (!intent.getVeg() &&  item.isVeg())  continue; // non-veg wanted → skip veg
            }

            if (intent.getMaxBudget() != null
                    && item.getPrice().doubleValue() > intent.getMaxBudget()) continue;

            if (intent.getMaxPrepTime() != null
                    && item.getPrepTimeMinutes() > intent.getMaxPrepTime()) continue;

            if (intent.getMealType() != null
                    && !"ANY".equalsIgnoreCase(item.getMealType())
                    && !intent.getMealType().equalsIgnoreCase(item.getMealType())) continue;

            if (intent.getExcludeIngredients() != null) {
                boolean excluded = false;
                for (String ex : intent.getExcludeIngredients()) {
                    String exL = ex.toLowerCase();
                    if (exL.equals("onion") && item.isContainsOnion()) { excluded = true; break; }
                    if (exL.equals("nuts")  && item.isContainsNuts())  { excluded = true; break; }
                }
                if (excluded) continue;
            }

            // ── Soft scoring (boost items that match preferences) ──────────

            int score = 50; // base score
            List<String> reasons = new ArrayList<>();

            if (intent.getSpicePreference() != null
                    && intent.getSpicePreference().equalsIgnoreCase(item.getSpiceLevel())) {
                score += 20;
                reasons.add("Matches your " + intent.getSpicePreference().toLowerCase() + " spice preference");
            }

            if (item.getRating() >= 4.5) {
                score += 15;
                reasons.add("Highly rated (" + item.getRating() + "★)");
            } else if (item.getRating() >= 4.0) {
                score += 10;
                reasons.add("Well rated (" + item.getRating() + "★)");
            }

            if (item.getPrepTimeMinutes() <= 10) {
                score += 10;
                reasons.add("Ready in just " + item.getPrepTimeMinutes() + " minutes");
            }

            if (intent.getMaxBudget() != null
                    && item.getPrice().doubleValue() <= intent.getMaxBudget() * 0.7) {
                score += 5;
                reasons.add("Great value at ₹" + item.getPrice().toPlainString());
            }

            String reason = reasons.isEmpty() ? "Matches your criteria" : String.join(". ", reasons);
            results.add(new MenuRecommendation(item, reason, Math.min(100, score)));
        }

        // Sort: highest score first, then by rating
        results.sort(Comparator
            .comparingInt(MenuRecommendation::getMatchScore).reversed()
            .thenComparingDouble(r -> -r.getItem().getRating()));

        int limit = Math.max(1, Math.min(intent.getQuantity() > 1 ? intent.getQuantity() : 3, 5));
        return results.stream().limit(limit).collect(Collectors.toList());
    }
}
