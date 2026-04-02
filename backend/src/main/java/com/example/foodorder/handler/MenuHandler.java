package com.example.foodorder.handler;

import com.example.foodorder.server.BaseHandler;
import com.example.foodorder.service.MenuService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * GET /api/menu — returns the full menu as a JSON array.
 *
 * Replaces the Spring @RestController @GetMapping from MenuController.java.
 * Extends BaseHandler to inherit CORS, error handling, and JSON helpers.
 */
public class MenuHandler extends BaseHandler {

    private final MenuService menuService;

    public MenuHandler(MenuService menuService) {
        this.menuService = menuService;
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        sendJson(exchange, menuService.findAll());
    }
}
