package com.example.foodorder.service;

import com.example.foodorder.dto.ParsedFoodIntent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simulates an LLM intent parser using keyword-matching rules.
 *
 * In a production system this class would call an LLM API (Gemini, OpenAI, etc.)
 * and parse the structured JSON response into a ParsedFoodIntent.
 * Here we approximate that behaviour with regex and string matching.
 *
 * OOP concepts:
 *   - Single Responsibility: only responsible for converting text → ParsedFoodIntent
 *   - Encapsulation: all regex patterns are private
 */
public class LlmIntentParserService {

    // ── Compiled patterns (reused across calls for efficiency) ──────────────
    private static final Pattern BUDGET_PATTERN =
        Pattern.compile("(?:under|below|budget|max)\\s*\\$?(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TIME_PATTERN   =
        Pattern.compile("(\\d+)\\s*min",  Pattern.CASE_INSENSITIVE);
    private static final Pattern QTY_PATTERN    =
        Pattern.compile("(\\d+)\\s*(?:items?|options?|dishes?)", Pattern.CASE_INSENSITIVE);

    /**
     * Parse a natural-language food request into a structured intent.
     *
     * @param userMessage raw message from the user, e.g. "I want quick veg snacks under 150"
     * @return ParsedFoodIntent with all detected preferences
     */
    public ParsedFoodIntent parseIntent(String userMessage) {
        ParsedFoodIntent intent = new ParsedFoodIntent();
        String lower = userMessage.toLowerCase();

        // ── Veg / Non-veg detection ─────────────────────────────────────────
        boolean mentionsVeg    = lower.contains("veg") || lower.contains("vegetarian");
        boolean mentionsNonVeg = lower.contains("non-veg") || lower.contains("nonveg")
                                 || lower.contains("chicken") || lower.contains("mutton")
                                 || lower.contains("fish")    || lower.contains("meat");

        if (mentionsVeg && mentionsNonVeg) {
            intent.setContradictory(true);
            intent.setClarificationNote(
                "You mentioned both veg and non-veg (e.g., 'veg chicken'). " +
                "Showing veg options since 'veg' appeared first.");
            intent.setVeg(true);
        } else if (mentionsVeg) {
            intent.setVeg(true);
        } else if (mentionsNonVeg) {
            intent.setVeg(false);
        }

        // ── Budget ──────────────────────────────────────────────────────────
        Matcher budgetMatcher = BUDGET_PATTERN.matcher(lower);
        if (budgetMatcher.find()) {
            intent.setMaxBudget(Double.parseDouble(budgetMatcher.group(1)));
        }

        // ── Spice level ─────────────────────────────────────────────────────
        if (lower.contains("extra spicy") || lower.contains("very spicy")) {
            intent.setSpicePreference("EXTRA_SPICY");
        } else if (lower.contains("spicy")) {
            intent.setSpicePreference("SPICY");
        } else if (lower.contains("mild")) {
            intent.setSpicePreference("MILD");
        } else if (lower.contains("medium spice")) {
            intent.setSpicePreference("MEDIUM");
        }

        // ── Prep time ───────────────────────────────────────────────────────
        Matcher timeMatcher = TIME_PATTERN.matcher(lower);
        if (timeMatcher.find()) {
            intent.setMaxPrepTime(Integer.parseInt(timeMatcher.group(1)));
        } else if (lower.contains("quick") || lower.contains("fast")) {
            intent.setMaxPrepTime(15);
        }

        // ── Ingredient exclusions ───────────────────────────────────────────
        List<String> exclusions = new ArrayList<>();
        if (lower.contains("no onion")    || lower.contains("without onion")) exclusions.add("onion");
        if (lower.contains("no nuts")     || lower.contains("nut free")
                                          || lower.contains("nut-free"))      exclusions.add("nuts");
        if (lower.contains("no garlic")   || lower.contains("without garlic")) exclusions.add("garlic");
        intent.setExcludeIngredients(exclusions);

        // ── Meal type ───────────────────────────────────────────────────────
        if      (lower.contains("breakfast")) intent.setMealType("BREAKFAST");
        else if (lower.contains("lunch"))     intent.setMealType("LUNCH");
        else if (lower.contains("dinner"))    intent.setMealType("DINNER");
        else if (lower.contains("snack"))     intent.setMealType("SNACK");

        // ── Requested quantity ──────────────────────────────────────────────
        Matcher qtyMatcher = QTY_PATTERN.matcher(lower);
        if (qtyMatcher.find()) {
            intent.setQuantity(Math.min(5, Integer.parseInt(qtyMatcher.group(1))));
        }

        // ── Ambiguity check ─────────────────────────────────────────────────
        if (intent.getVeg() == null && intent.getMaxBudget() == null
                && intent.getSpicePreference() == null && intent.getMaxPrepTime() == null
                && intent.getMealType() == null && exclusions.isEmpty()) {
            intent.setAmbiguous(true);
            intent.setClarificationNote("Your request is quite broad. Showing our top-rated dishes!");
        }

        return intent;
    }
}
