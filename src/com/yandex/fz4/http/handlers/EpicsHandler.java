
package com.yandex.fz4.http.handlers;


import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.fz4.model.Epic;
import com.yandex.fz4.service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;


    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (path.matches("/epics/\\d+/subtasks")) {
                        handleGetEpicSubtasks(exchange);
                    } else if (path.matches("/epics/\\d+")) {
                        handleGetEpicById(exchange);
                    } else {
                        handleGetAllEpics(exchange);
                    }
                    break;
                case "POST":
                    handleCreateEpic(exchange);
                    break;
                case "DELETE":
                    if (path.matches("/epics/\\d+")) {
                        handleDeleteEpic(exchange);
                    } else {
                        sendBadRequest(exchange, "Invalid path for DELETE");
                    }
                    break;
                default:
                    sendBadRequest(exchange, "Method not allowed");
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getEpics());
        sendText(exchange, response);
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = extractIdFromPath(exchange);
        if (idOpt.isEmpty()) {
            sendBadRequest(exchange, "Invalid epic ID");
            return;
        }

        Epic epic = taskManager.getEpicById(idOpt.get());
        if (epic == null) {
            sendNotFound(exchange);
            return;
        }

        String response = gson.toJson(epic);
        sendText(exchange, response);
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = extractIdFromPath(exchange);
        if (idOpt.isEmpty()) {
            sendBadRequest(exchange, "Invalid epic ID");
            return;
        }

        String response = gson.toJson(taskManager.getSubtasksByEpicId(idOpt.get()));
        sendText(exchange, response);
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        Optional<String> bodyOpt = readRequestBody(exchange);
        if (bodyOpt.isEmpty()) {
            sendBadRequest(exchange, "Empty request body");
            return;
        }

        try {
            Epic epic = gson.fromJson(bodyOpt.get(), Epic.class);
            Epic createdEpic = taskManager.addEpic(epic);
            String response = gson.toJson(createdEpic);
            sendCreated(exchange, response);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Invalid JSON format");
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = extractIdFromPath(exchange);
        if (idOpt.isEmpty()) {
            sendBadRequest(exchange, "Invalid epic ID");
            return;
        }

        taskManager.removeEpicById(idOpt.get());
        sendText(exchange, "{\"message\": \"Epic deleted successfully\"}");
    }
}