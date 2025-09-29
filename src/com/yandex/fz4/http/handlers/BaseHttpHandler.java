package com.yandex.fz4.http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.fz4.model.*;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

public class BaseHttpHandler {
    protected final Gson gson;

    public BaseHttpHandler() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 200);
    }

    protected void sendCreated(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 201);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "{\"error\": \"Not found\"}";
        sendText(exchange, response, 404);
    }

    protected void sendHasOverlaps(HttpExchange exchange) throws IOException {
        String response = "{\"error\": \"Task overlaps with existing tasks\"}";
        sendText(exchange, response, 406);
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        String response = "{\"error\": \"Internal server error\"}";
        sendText(exchange, response, 500);
    }

    protected void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        String response = "{\"error\": \"" + message + "\"}";
        sendText(exchange, response, 400);
    }

    protected Optional<String> readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            return Optional.of(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    protected Optional<Integer> extractIdFromPath(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length < 3) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(pathParts[pathParts.length - 1]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}