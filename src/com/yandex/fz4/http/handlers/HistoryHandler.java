
package com.yandex.fz4.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.fz4.service.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;


    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendBadRequest(exchange, "Only GET method is allowed");
            return;
        }

        try {
            String response = gson.toJson(taskManager.getHistory());
            sendText(exchange, response);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}