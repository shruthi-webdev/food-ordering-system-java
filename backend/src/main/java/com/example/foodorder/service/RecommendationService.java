package com.example.foodorder.service;

import com.example.foodorder.dto.ChatRecommendationResponse;
import com.example.foodorder.dto.MenuRecommendation;
import com.example.foodorder.dto.ParsedFoodIntent;
import com.example.foodorder.model.MenuItem;

import java.util.List;

/**
 * Orchestrator service: coordinates intent parsing → rule engine → response building.
 *
 * OOP concepts:
 *   - Composition over inheritance: holds references to two collaborating services
 *   - Single Responsibility: only orchestrates; no parsing or rule logic here
 *   - Constructor injection (manual DI): dependencies provided by Main.java
 */
public class RecommendationService {

    private final LlmIntentParserService intentParser;
    private final FoodRuleEngineService  ruleEngine;
    private final MenuService            menuService;

    public RecommendationService(LlmIntentParserService intentParser,
                                 FoodRuleEngineService  ruleEngine,
                                 MenuService            menuService) {
        this.intentParser = intentParser;
        this.ruleEngine   = ruleEngine;
        this.menuService  = menuService;
    }

    /**
     * Full pipeline:
     * 1. Parse natural-language message into structured intent
     * 2. Fetch full menu of extended items
     * 3. Apply business rules to filter and rank
     * 4. Build human-readable response message
     *
     * @param userMessage the user's raw chat message
     * @return ChatRecommendationResponse with intent, recommendations, and message
     */
    public ChatRecommendationResponse getRecommendations(String userMessage) {

        // Step 1: Parse intent
        ParsedFoodIntent intent = intentParser.parseIntent(userMessage);

        // Step 2: Get extended (chat-assistant) menu
        List<MenuItem> allItems = menuService.findAllExtended();

        // Step 3: Apply rules
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
            int n = recommendations.size();
            message = "Found " + n + " dish" + (n > 1 ? "es" : "") + " for you!";
        }

        return new ChatRecommendationResponse(intent, recommendations, message);
    }
}
