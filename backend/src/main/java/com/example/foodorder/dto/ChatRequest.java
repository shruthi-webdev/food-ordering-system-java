package com.example.foodorder.dto;

/**
 * Request body for POST /api/chat/recommend
 */
public class ChatRequest {
    private String message;

    public ChatRequest() {}
    public ChatRequest(String message) { this.message = message; }

    public String message()              { return message; }
    public void setMessage(String message) { this.message = message; }
}
