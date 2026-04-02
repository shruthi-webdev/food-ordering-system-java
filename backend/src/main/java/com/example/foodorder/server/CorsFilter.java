package com.example.foodorder.server;

import com.sun.net.httpserver.HttpExchange;

/**
 * Adds CORS headers to every HTTP response.
 *
 * This replaces Spring's @CrossOrigin annotation.
 * Called by BaseHandler.handle() before any response is sent,
 * ensuring the frontend at http://localhost:5173 can reach the backend.
 */
public final class CorsFilter {

    private CorsFilter() { /* static utility class */ }

    /** Injects standard CORS headers into the response. */
    public static void apply(HttpExchange exchange) {
        var headers = exchange.getResponseHeaders();
        headers.set("Access-Control-Allow-Origin",  "http://localhost:5173");
        headers.set("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        headers.set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        headers.set("Access-Control-Max-Age",       "86400");
    }
}
