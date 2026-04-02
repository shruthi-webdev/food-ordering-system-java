package com.example.foodorder.server;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

/**
 * Custom handler interface — replaces Spring's @RestController.
 *
 * OOP concept: Interface
 *   Any class that wants to handle HTTP requests must implement this contract.
 *   This mirrors how Spring's DispatcherServlet dispatches to controllers,
 *   but using pure Java with no annotations.
 */
public interface HttpHandler {

    /**
     * Handle an incoming HTTP request.
     *
     * @param exchange the HTTP exchange containing request + response streams
     * @throws IOException if the response cannot be written
     */
    void handle(HttpExchange exchange) throws IOException;
}
