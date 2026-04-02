package com.example.foodorder.server;

import com.example.foodorder.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Abstract base class for all HTTP route handlers.
 *
 * OOP concepts demonstrated:
 *   - Abstract class: cannot be instantiated directly; provides shared behaviour
 *   - Inheritance: all concrete handlers (MenuHandler, CartHandler, etc.) extend this
 *   - Encapsulation: low-level HTTP plumbing is hidden from subclasses
 *   - Template method pattern: handle() dispatches to get()/post()/delete()
 *
 * Implements our custom HttpHandler interface and also implements
 * com.sun.net.httpserver.HttpHandler so it can be registered on the JDK server.
 */
public abstract class BaseHandler
        implements HttpHandler, com.sun.net.httpserver.HttpHandler {

    // ─────────────────────────────────────────────────────
    // Template method — dispatches by HTTP verb
    // ─────────────────────────────────────────────────────

    @Override
    public final void handle(HttpExchange exchange) throws IOException {
        // CORS pre-flight
        CorsFilter.apply(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendEmpty(exchange, 204);
            return;
        }

        try {
            String method = exchange.getRequestMethod().toUpperCase();
            switch (method) {
                case "GET"    -> get(exchange);
                case "POST"   -> post(exchange);
                case "DELETE" -> delete(exchange);
                default       -> sendError(exchange, 405, "Method Not Allowed");
            }
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            sendError(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────
    // Verb hooks — subclasses override what they need
    // ─────────────────────────────────────────────────────

    protected void get(HttpExchange exchange) throws IOException {
        sendError(exchange, 405, "Method Not Allowed");
    }

    protected void post(HttpExchange exchange) throws IOException {
        sendError(exchange, 405, "Method Not Allowed");
    }

    protected void delete(HttpExchange exchange) throws IOException {
        sendError(exchange, 405, "Method Not Allowed");
    }

    // ─────────────────────────────────────────────────────
    // Response helpers (encapsulated I/O boilerplate)
    // ─────────────────────────────────────────────────────

    /** Serialize any object to JSON and send 200 response. */
    protected void sendJson(HttpExchange exchange, Object body) throws IOException {
        sendJson(exchange, 200, body);
    }

    /** Serialize any object to JSON and send with custom status. */
    protected void sendJson(HttpExchange exchange, int status, Object body) throws IOException {
        byte[] bytes = JsonUtil.toJson(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /** Send a plain-text error response. */
    protected void sendError(HttpExchange exchange, int status, String message) throws IOException {
        String json = "{\"error\":" + "\"" + message.replace("\"", "'") + "\"}";
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /** Send an empty 204 No Content response (used for OPTIONS pre-flight & DELETEs). */
    protected void sendEmpty(HttpExchange exchange, int status) throws IOException {
        exchange.sendResponseHeaders(status, -1);
        exchange.getResponseBody().close();
    }

    // ─────────────────────────────────────────────────────
    // Request helpers
    // ─────────────────────────────────────────────────────

    /** Read the full request body as a UTF-8 string. */
    protected String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Extract the last path segment from the URI.
     * e.g. /api/cart/biryani  →  "biryani"
     */
    protected String lastPathSegment(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        return parts.length > 0 ? parts[parts.length - 1] : "";
    }

    /** Parse flat JSON body into a key→value map. */
    protected Map<String, String> parseJsonBody(HttpExchange exchange) throws IOException {
        return JsonUtil.parseFlat(readBody(exchange));
    }
}
