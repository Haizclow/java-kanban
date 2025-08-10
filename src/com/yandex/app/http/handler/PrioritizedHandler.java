package com.yandex.app.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;
import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(gson);
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Task> prioritized = taskManager.getPrioritizedTasks();
                sendSuccess(exchange, gson.toJson(prioritized));
            } else {
                sendMethodNotAllowed(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}
