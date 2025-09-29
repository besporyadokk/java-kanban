
package com.yandex.fz4.http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.fz4.model.Task;
import com.yandex.fz4.service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (path.matches("/tasks/\\d+")) {
                        handleGetTaskById(exchange);
                    } else {
                        handleGetAllTasks(exchange);
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateTask(exchange);
                    break;
                case "DELETE":
                    if (path.matches("/tasks/\\d+")) {
                        handleDeleteTask(exchange);
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

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getTasks());
        sendText(exchange, response);
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = extractIdFromPath(exchange);
        if (idOpt.isEmpty()) {
            sendBadRequest(exchange, "Invalid task ID");
            return;
        }

        Task task = taskManager.getTaskById(idOpt.get());
        if (task == null) {
            sendNotFound(exchange);
            return;
        }

        String response = gson.toJson(task);
        sendText(exchange, response);
    }

    private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
        Optional<String> bodyOpt = readRequestBody(exchange);
        if (bodyOpt.isEmpty()) {
            sendBadRequest(exchange, "Empty request body");
            return;
        }

        try {
            Task task = gson.fromJson(bodyOpt.get(), Task.class);

            if (task.getId() == 0) { // Create new task
                try {
                    Task createdTask = taskManager.addTask(task);
                    String response = gson.toJson(createdTask);
                    sendCreated(exchange, response);
                } catch (IllegalArgumentException e) {
                    sendHasOverlaps(exchange);
                }
            } else { // Update existing task
                try {
                    taskManager.updateTask(task);
                    sendCreated(exchange, "{\"message\": \"Task updated successfully\"}");
                } catch (IllegalArgumentException e) {
                    sendHasOverlaps(exchange);
                }
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Invalid JSON format");
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = extractIdFromPath(exchange);
        if (idOpt.isEmpty()) {
            sendBadRequest(exchange, "Invalid task ID");
            return;
        }

        taskManager.removeTaskById(idOpt.get());
        sendText(exchange, "{\"message\": \"Task deleted successfully\"}");
    }
}