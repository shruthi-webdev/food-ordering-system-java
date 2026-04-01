package com.example.foodorder.service;

import com.example.foodorder.dto.MenuRecommendation;
import com.example.foodorder.dto.ParsedFoodIntent;
import com.example.foodorder.model.MenuItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Pure Java rule engine. Filters and ranks menu items based on the parsed intent.
 * All business logic lives here — the LLM has NO say in which dishes are returned.
 */
@Service
public class FoodRuleEngineService {

    /**
     * Apply all rules to filter and rank menu items against the user's intent.
     *
     * @param intent    structured intent parsed from user's message
     * @param menuItems full list of available menu items
     * @return sorted list of recommendations with reasons and scores
     */
    public List<MenuRecommendation> applyRules(ParsedFoodIntent intent, List<MenuItem> menuItems) {
        List<MenuRecommendation> results = new ArrayList<>();

        for (MenuItem item : menuItems) {
            // --- Hard filters (must pass ALL to be included) ---

            // Filter: must be available and in stock
            if (!item.isAvailable() || item.getStockCount() <= 0) continue;

            // Filter: veg preference
            if (intent.getVeg() != null) {
                if (intent.getVeg() && !item.isVeg()) continue;
                if (!intent.getVeg() && item.isVeg()) continue; // non-veg requested
            }

            // Filter: budget
            if (intent.getMaxBudget() != null) {
                if (item.getPrice().doubleValue() > intent.getMaxBudget()) continue;
            }

            // Filter: prep time
            if (intent.getMaxPrepTime() != null) {
                if (item.getPrepTimeMinutes() > intent.getMaxPrepTime()) continue;
            }

            // Filter: ingredient exclusions
            if (intent.getExcludeIngredients() != null) {
                boolean excluded = false;
                for (String exclusion : intent.getExcludeIngredients()) {
                    String ex = exclusion.toLowerCase();
                    if (ex.equals("onion") && item.isContainsOnion()) { excluded = true; break; }
                    if (ex.equals("nuts") && item.isContainsNuts()) { excluded = true; break; }
                }
                if (excluded) continue;
            }

            // Filter: meal type
            if (intent.getMealType() != null && !"ANY".equals(item.getMealType())) {
                if (!intent.getMealType().equalsIgnoreCase(item.getMealType())) continue;
            }

            // --- Scoring (soft criteria) ---
            int score = 50; // base score
            List<String> reasons = new ArrayList<>();

            // Boost for matching spice preference
            if (intent.getSpicePreference() != null
                    && intent.getSpicePreference().equalsIgnoreCase(item.getSpiceLevel())) {
                score += 20;
                reasons.add("Matches your " + intent.getSpicePreference().toLowerCase() + " spice preference");
            }

            // Boost for rating
            if (item.getRating() >= 4.5) {
                score += 15;
                reasons.add("Highly rated (" + item.getRating() + "★)");
            } else if (item.getRating() >= 4.0) {
                score += 10;
                reasons.add("Well rated (" + item.getRating() + "★)");
            }

            // Boost for quick prep
            if (item.getPrepTimeMinutes() <= 10) {
                score += 10;
                reasons.add("Ready in just " + item.getPrepTimeMinutes() + " minutes");
            }

            // Boost for budget friendliness
            if (intent.getMaxBudget() != null
                    && item.getPrice().doubleValue() <= intent.getMaxBudget() * 0.7) {
                score += 5;
                reasons.add("Great value at $" + item.getPrice());
            }

            // Build reason string
            String reason = reasons.isEmpty()
                    ? "Matches your criteria"
                    : String.join(". ", reasons);

            results.add(new MenuRecommendation(item, reason, Math.min(100, score)));
        }

        // Sort by score descending, then by rating descending
        results.sort(Comparator
                .comparingInt(MenuRecommendation::getMatchScore).reversed()
                .thenComparingDouble(r -> -r.getItem().getRating()));

        // Return top N (based on requested quantity, default 3)
        int limit = Math.max(1, Math.min(intent.getQuantity() > 1 ? intent.getQuantity() : 3, 5));
        return results.stream().limit(limit).collect(Collectors.toList());
    }
}
