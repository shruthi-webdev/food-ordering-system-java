package com.example.foodorder.server;

import com.sun.net.httpserver.HttpServer;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple path-based router.
 *
 * Maps URL prefixes to BaseHandler instances and registers them on the
 * JDK HttpServer. This is the pure-Java equivalent of Spring's @RequestMapping.
 *
 * OOP concept: Encapsulation — the internal route map is private;
 * routes are added via a fluent add() method.
 */
public final class Router {

    private final Map<String, BaseHandler> routes = new HashMap<>();

    /**
     * Register a handler for a URL path prefix.
     *
     * @param path    URL prefix, e.g. "/api/menu"
     * @param handler handler that will receive all requests matching this prefix
     * @return this Router (fluent API)
     */
    public Router add(String path, BaseHandler handler) {
        routes.put(path, handler);
        return this;
    }

    /**
     * Register all routes on the given HttpServer.
     * Must be called after all add() calls, before server.start().
     */
    public void register(HttpServer server) {
        for (Map.Entry<String, BaseHandler> entry : routes.entrySet()) {
            server.createContext(entry.getKey(), entry.getValue());
        }
    }
}
