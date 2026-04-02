package com.example.foodorder.handler;

import com.example.foodorder.dto.ChatRecommendationResponse;
import com.example.foodorder.server.BaseHandler;
import com.example.foodorder.service.RecommendationService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

/**
 * Chat assistant endpoint — replaces Spring's ChatController.
 *
 * Routes:
 *   POST /api/chat/recommend  → parse message → recommend → return JSON
 *
 * Delegates all logic to RecommendationService (which in turn uses
 * LlmIntentParserService + FoodRuleEngineService), keeping the handler thin.
 */
public class ChatHandler extends BaseHandler {

    private final RecommendationService recommendationService;

    public ChatHandler(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Override
    protected void post(HttpExchange exchange) throws IOException {
        Map<String, String> body = parseJsonBody(exchange);

        String message = body.get("message");
        if (message == null || message.isBlank()) {
            sendError(exchange, 400, "message cannot be empty");
            return;
        }

        ChatRecommendationResponse response = recommendationService.getRecommendations(message);
        sendJson(exchange, response);
    }
}
