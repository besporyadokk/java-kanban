
package com.yandex.fz4.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.fz4.service.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendBadRequest(exchange, "Only GET method is allowed");
            return;
        }

        try {
            String response = gson.toJson(taskManager.getPrioritizedTasks());
            sendText(exchange, response);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}