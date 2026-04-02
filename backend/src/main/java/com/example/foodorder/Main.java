package com.example.foodorder;

import com.example.foodorder.handler.CartHandler;
import com.example.foodorder.handler.ChatHandler;
import com.example.foodorder.handler.MenuHandler;
import com.example.foodorder.handler.OrderHandler;
import com.example.foodorder.server.Router;
import com.example.foodorder.service.*;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Application entry point — pure Java, no frameworks.
 *
 * Responsibilities:
 *   1. Manually wires all services (manual Dependency Injection)
 *   2. Creates the JDK built-in HttpServer on port 8080
 *   3. Registers route handlers via a custom Router
 *   4. Assigns a thread pool to handle concurrent requests
 *   5. Registers a shutdown hook to cleanly stop threads
 *
 * OOP concepts shown here:
 *   - Manual DI (constructor injection — no Spring, no IoC container)
 *   - Polymorphism: orderProcessor variable is typed as OrderProcessor
 *     interface but holds an AsyncOrderProcessor instance
 *   - Threads: 4-thread ExecutorService for handling HTTP requests
 *   - Packages: all classes imported from their respective packages
 */
public class Main {

    private static final int PORT = 8080;
    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws Exception {

        // ── Step 1: Wire services (Manual Dependency Injection) ─────────────
        MenuService           menuService    = new MenuService();
        CartService           cartService    = new CartService(menuService);
        BillingService        billingService = new BillingService();

        // Polymorphism: variable typed as interface, holds async implementation
        OrderProcessor        orderProcessor = new AsyncOrderProcessor(billingService);

        LlmIntentParserService intentParser  = new LlmIntentParserService();
        FoodRuleEngineService  ruleEngine    = new FoodRuleEngineService();
        RecommendationService  recService    = new RecommendationService(intentParser, ruleEngine, menuService);

        // ── Step 2: Create JDK built-in HTTP server ─────────────────────────
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), /*backlog=*/ 50);

        // ── Step 3: Register route handlers ─────────────────────────────────
        new Router()
            .add("/api/menu",             new MenuHandler(menuService))
            .add("/api/cart",             new CartHandler(cartService))
            .add("/api/order/checkout",   new OrderHandler(cartService, orderProcessor))
            .add("/api/chat/recommend",   new ChatHandler(recService))
            .register(server);

        // ── Step 4: Assign a thread pool for handling concurrent requests ────
        // Thread concept: ExecutorService manages a pool of 4 worker threads
        server.setExecutor(Executors.newFixedThreadPool(4, r -> {
            Thread t = new Thread(r);
            t.setName("http-worker-" + t.getId());
            return t;
        }));

        // ── Step 5: Graceful shutdown hook ───────────────────────────────────
        // Thread concept: shutdown hook runs in a separate JVM thread on exit
        AsyncOrderProcessor asyncProc = (AsyncOrderProcessor) orderProcessor;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down server...");
            server.stop(2);       // allow 2 s for in-flight requests to complete
            asyncProc.shutdown(); // stop billing thread pool
            log.info("Server stopped.");
        }, "shutdown-hook"));

        // ── Start ────────────────────────────────────────────────────────────
        server.start();
        log.info("=================================================");
        log.info("  Food Ordering Backend started on port " + PORT);
        log.info("  Frontend: http://localhost:5173");
        log.info("  API base: http://localhost:" + PORT + "/api");
        log.info("=================================================");
    }
}
