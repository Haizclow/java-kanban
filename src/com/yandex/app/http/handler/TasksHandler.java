package com.yandex.app.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;
import java.io.IOException;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(gson);
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    sendMethodNotAllowed(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.matches("/tasks/\\d+")) {
            try {
                int id = Integer.parseInt(path.split("/")[2]);
                Task task = taskManager.getTaskById(id);
                if (task != null) {
                    sendSuccess(exchange, gson.toJson(task));
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendBadRequest(exchange);
            }
        } else {
            List<Task> tasks = taskManager.getTasks();
            sendSuccess(exchange, gson.toJson(tasks));
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        Task task = parseJson(exchange, Task.class);
        if (task == null) {
            sendBadRequest(exchange);
            return;
        }

        try {
            if (task.getId() == 0) {
                int id = taskManager.addTask(task);
                sendCreated(exchange, "{\"id\":" + id + "}");
            } else {
                taskManager.updateTask(task);
                sendSuccess(exchange, "{\"status\":\"updated\"}");
            }
        } catch (Exception e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            taskManager.deleteTasks();
            sendSuccess(exchange, "{\"status\":\"all tasks deleted\"}");
        } else {
            try {
                int id = Integer.parseInt(query.split("=")[1]);
                taskManager.deleteTaskById(id);
                sendSuccess(exchange, "{\"status\":\"task deleted\"}");
            } catch (NumberFormatException e) {
                sendBadRequest(exchange);
            }
        }
    }
}