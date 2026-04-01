package com.example.foodorder.controller;

import com.example.foodorder.dto.ChatRecommendationResponse;
import com.example.foodorder.dto.ChatRequest;
import com.example.foodorder.service.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Chat assistant endpoint.
 * POST /api/chat/recommend
 */
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/chat")
public class ChatController {

    private final RecommendationService recommendationService;

    public ChatController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/recommend")
    public ChatRecommendationResponse recommend(@RequestBody ChatRequest request) {
        if (request.message() == null || request.message().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message cannot be empty");
        }
        return recommendationService.getRecommendations(request.message());
    }
}
