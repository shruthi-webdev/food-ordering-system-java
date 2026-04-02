package com.example.foodorder.dto;

import java.util.List;

/**
 * Full response body for POST /api/chat/recommend
 */
public class ChatRecommendationResponse {

    private final ParsedFoodIntent        parsedIntent;
    private final List<MenuRecommendation> recommendations;
    private final String                  message;

    public ChatRecommendationResponse(ParsedFoodIntent parsedIntent,
                                      List<MenuRecommendation> recommendations,
                                      String message) {
        this.parsedIntent    = parsedIntent;
        this.recommendations = recommendations;
        this.message         = message;
    }

    public ParsedFoodIntent         getParsedIntent()    { return parsedIntent; }
    public List<MenuRecommendation> getRecommendations() { return recommendations; }
    public String                   getMessage()         { return message; }
}
