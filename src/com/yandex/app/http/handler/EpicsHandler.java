package com.yandex.app.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.model.Epic;
import com.yandex.app.service.TaskManager;
import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
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
        if (path.matches("/epics/\\d+")) {
            try {
                int id = Integer.parseInt(path.split("/")[2]);
                Epic epic = taskManager.getEpicById(id);
                if (epic != null) {
                    sendSuccess(exchange, gson.toJson(epic));
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendBadRequest(exchange);
            }
        } else {
            List<Epic> epics = taskManager.getEpics();
            sendSuccess(exchange, gson.toJson(epics));
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        Epic epic = parseJson(exchange, Epic.class);
        if (epic == null) {
            sendBadRequest(exchange);
            return;
        }

        if (epic.getId() == 0) {
            int id = taskManager.addEpic(epic);
            sendCreated(exchange, "{\"id\":" + id + "}");
        } else {
            taskManager.updateEpic(epic);
            sendSuccess(exchange, "{\"status\":\"updated\"}");
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            taskManager.deleteEpics();
            sendSuccess(exchange, "{\"status\":\"all epics deleted\"}");
        } else {
            try {
                int id = Integer.parseInt(query.split("=")[1]);
                taskManager.deleteEpicById(id);
                sendSuccess(exchange, "{\"status\":\"epic deleted\"}");
            } catch (NumberFormatException e) {
                sendBadRequest(exchange);
            }
        }
    }
}
