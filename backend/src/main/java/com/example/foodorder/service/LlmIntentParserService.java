package com.example.foodorder.service;

import com.example.foodorder.dto.ParsedFoodIntent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * Parses natural-language food requests into structured ParsedFoodIntent JSON.
 *
 * IMPORTANT: The LLM only extracts intent. It NEVER recommends or invents dishes.
 *
 * In production, replace the simulate() method with an actual LLM API call
 * (e.g., OpenAI, Gemini, or a local model).
 */
@Service
public class LlmIntentParserService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * The strict system prompt sent to the LLM.
     * Forces JSON-only output with no dish names.
     */
    private static final String SYSTEM_PROMPT = """
        You are a food order intent parser. Your ONLY job is to extract structured intent
        from a user's natural-language food request.

        RULES:
        1. You must ONLY return valid JSON matching the schema below.
        2. You must NEVER suggest, recommend, or name any specific dish.
        3. If the user's request is ambiguous, set "ambiguous": true and add a "clarificationNote".
        4. If the user's request is contradictory (e.g., "veg chicken"), set "contradictory": true
           and add a "clarificationNote" explaining the conflict.
        5. Use null for any field the user did not mention.
        6. "spicePreference" must be one of: MILD, MEDIUM, SPICY, EXTRA_SPICY, or null.
        7. "mealType" must be one of: BREAKFAST, LUNCH, DINNER, SNACK, or null.
        8. "quantity" defaults to 1 if not specified.

        JSON SCHEMA:
        {
          "veg": boolean | null,
          "maxBudget": number | null,
          "spicePreference": "MILD" | "MEDIUM" | "SPICY" | "EXTRA_SPICY" | null,
          "maxPrepTime": number | null,
          "excludeIngredients": ["string"] | [],
          "mealType": "BREAKFAST" | "LUNCH" | "DINNER" | "SNACK" | null,
          "quantity": number,
          "ambiguous": boolean,
          "contradictory": boolean,
          "clarificationNote": "string" | null
        }

        Respond with ONLY the JSON object. No markdown, no explanation, no dish names.
        """;

    /**
     * In production, this method would:
     * 1. Build a prompt with SYSTEM_PROMPT + user message
     * 2. Call the LLM API (OpenAI, Gemini, etc.)
     * 3. Parse the JSON response into ParsedFoodIntent
     *
     * For now, it uses keyword-based simulation.
     */
    public ParsedFoodIntent parseIntent(String userMessage) {
        // TODO: Replace with actual LLM API call in production
        // Example with OpenAI:
        //   String response = openAiClient.chat(SYSTEM_PROMPT, userMessage);
        //   return objectMapper.readValue(response, ParsedFoodIntent.class);

        return simulateIntentParsing(userMessage);
    }

    public String getSystemPrompt() {
        return SYSTEM_PROMPT;
    }

    /**
     * Keyword-based simulation of LLM intent parsing.
     * This is a fallback for when no LLM API is configured.
     */
    private ParsedFoodIntent simulateIntentParsing(String message) {
        ParsedFoodIntent intent = new ParsedFoodIntent();
        String lower = message.toLowerCase();

        // Detect veg/non-veg
        boolean mentionsVeg = lower.contains("veg") || lower.contains("vegetarian");
        boolean mentionsNonVeg = lower.contains("non-veg") || lower.contains("nonveg")
                || lower.contains("chicken") || lower.contains("mutton")
                || lower.contains("fish") || lower.contains("meat");

        if (mentionsVeg && mentionsNonVeg) {
            intent.setContradictory(true);
            intent.setClarificationNote("You mentioned both veg and non-veg items (e.g., 'veg chicken'). "
                    + "Showing veg options since 'veg' appeared first.");
            intent.setVeg(true);
        } else if (mentionsVeg) {
            intent.setVeg(true);
        } else if (mentionsNonVeg) {
            intent.setVeg(false);
        }

        // Detect budget
        java.util.regex.Matcher budgetMatcher =
                java.util.regex.Pattern.compile("under\\s*\\$?(\\d+)").matcher(lower);
        if (!budgetMatcher.find()) {
            budgetMatcher = java.util.regex.Pattern.compile("below\\s*\\$?(\\d+)").matcher(lower);
        }
        if (!budgetMatcher.find()) {
            budgetMatcher = java.util.regex.Pattern.compile("budget\\s*\\$?(\\d+)").matcher(lower);
        }
        if (budgetMatcher.find()) {
            intent.setMaxBudget(Double.parseDouble(budgetMatcher.group(1)));
        }

        // Detect spice
        if (lower.contains("extra spicy") || lower.contains("very spicy")) {
            intent.setSpicePreference("EXTRA_SPICY");
        } else if (lower.contains("spicy")) {
            intent.setSpicePreference("SPICY");
        } else if (lower.contains("mild")) {
            intent.setSpicePreference("MILD");
        } else if (lower.contains("medium spice")) {
            intent.setSpicePreference("MEDIUM");
        }

        // Detect prep time
        java.util.regex.Matcher timeMatcher =
                java.util.regex.Pattern.compile("(\\d+)\\s*min").matcher(lower);
        if (timeMatcher.find()) {
            intent.setMaxPrepTime(Integer.parseInt(timeMatcher.group(1)));
        } else if (lower.contains("quick") || lower.contains("fast")) {
            intent.setMaxPrepTime(15);
        }

        // Detect exclusions
        java.util.List<String> exclusions = new java.util.ArrayList<>();
        if (lower.contains("no onion") || lower.contains("without onion")) exclusions.add("onion");
        if (lower.contains("no nuts") || lower.contains("nut free") || lower.contains("nut-free")) exclusions.add("nuts");
        if (lower.contains("no garlic") || lower.contains("without garlic")) exclusions.add("garlic");
        intent.setExcludeIngredients(exclusions);

        // Detect meal type
        if (lower.contains("breakfast")) intent.setMealType("BREAKFAST");
        else if (lower.contains("lunch")) intent.setMealType("LUNCH");
        else if (lower.contains("dinner")) intent.setMealType("DINNER");
        else if (lower.contains("snack")) intent.setMealType("SNACK");

        // Detect quantity
        java.util.regex.Matcher qtyMatcher =
                java.util.regex.Pattern.compile("(\\d+)\\s*(items?|options?|dishes?)").matcher(lower);
        if (qtyMatcher.find()) {
            intent.setQuantity(Math.min(5, Integer.parseInt(qtyMatcher.group(1))));
        }

        // If nothing was detected, mark as ambiguous
        if (intent.getVeg() == null && intent.getMaxBudget() == null
                && intent.getSpicePreference() == null && intent.getMaxPrepTime() == null
                && intent.getMealType() == null && exclusions.isEmpty()) {
            intent.setAmbiguous(true);
            intent.setClarificationNote("Your request is quite broad. Showing our top-rated dishes!");
        }

        return intent;
    }
}
