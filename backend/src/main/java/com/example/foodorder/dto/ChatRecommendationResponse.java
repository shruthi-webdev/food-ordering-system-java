package com.example.foodorder.dto;

import java.util.List;


public class ChatRecommendationResponse {
    private final ParsedFoodIntent parsedIntent;
    private final List<MenuRecommendation> recommendations;
    private final String message; // system note (e.g. "No matches found", or clarification)

    public ChatRecommendationResponse(ParsedFoodIntent parsedIntent,
                                       List<MenuRecommendation> recommendations,
                                       String message) {
        this.parsedIntent = parsedIntent;
        this.recommendations = recommendations;
        this.message = message;
    }

    public ParsedFoodIntent getParsedIntent() { return parsedIntent; }
    public List<MenuRecommendation> getRecommendations() { return recommendations; }
    public String getMessage() { return message; }
}
