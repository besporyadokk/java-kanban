
package com.yandex.fz4.http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.fz4.model.Subtask;
import com.yandex.fz4.service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;


    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (path.matches("/subtasks/\\d+")) {
                        handleGetSubtaskById(exchange);
                    } else {
                        handleGetAllSubtasks(exchange);
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateSubtask(exchange);
                    break;
                case "DELETE":
                    if (path.matches("/subtasks/\\d+")) {
                        handleDeleteSubtask(exchange);
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

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getSubtasks());
        sendText(exchange, response);
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = extractIdFromPath(exchange);
        if (idOpt.isEmpty()) {
            sendBadRequest(exchange, "Invalid subtask ID");
            return;
        }

        Subtask subtask = taskManager.getSubtaskById(idOpt.get());
        if (subtask == null) {
            sendNotFound(exchange);
            return;
        }

        String response = gson.toJson(subtask);
        sendText(exchange, response);
    }

    private void handleCreateOrUpdateSubtask(HttpExchange exchange) throws IOException {
        Optional<String> bodyOpt = readRequestBody(exchange);
        if (bodyOpt.isEmpty()) {
            sendBadRequest(exchange, "Empty request body");
            return;
        }

        try {
            Subtask subtask = gson.fromJson(bodyOpt.get(), Subtask.class);

            if (subtask.getId() == 0) { // Create new subtask
                try {
                    Subtask createdSubtask = taskManager.addSubtask(subtask);
                    String response = gson.toJson(createdSubtask);
                    sendCreated(exchange, response);
                } catch (IllegalArgumentException e) {
                    sendHasOverlaps(exchange);
                }
            } else { // Update existing subtask
                try {
                    taskManager.updateSubtask(subtask);
                    sendCreated(exchange, "{\"message\": \"Subtask updated successfully\"}");
                } catch (IllegalArgumentException e) {
                    sendHasOverlaps(exchange);
                }
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Invalid JSON format");
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = extractIdFromPath(exchange);
        if (idOpt.isEmpty()) {
            sendBadRequest(exchange, "Invalid subtask ID");
            return;
        }

        taskManager.removeSubtaskById(idOpt.get());
        sendText(exchange, "{\"message\": \"Subtask deleted successfully\"}");
    }
}