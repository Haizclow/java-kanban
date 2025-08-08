package com.yandex.app.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpTaskServer {
    private final HttpServer server;
    final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.gson = new Gson();
        this.server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/tasks", new TasksHandler());
        server.createContext("/subtasks", new SubtasksHandler());
        server.createContext("/epics", new EpicsHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/prioritized", new PrioritizedHandler());
    }

    public void start() {
        server.start();
        System.out.println("HTTP Task Server started on port 8080");
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP Task Server stopped");
    }

    private abstract class BaseHttpHandler implements HttpHandler {
        protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
            byte[] response = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }

        protected void sendNotFound(HttpExchange exchange) throws IOException {
            sendText(exchange, "{\"error\":\"Not Found\"}", 404);
        }

        protected void sendHasInteractions(HttpExchange exchange) throws IOException {
            sendText(exchange, "{\"error\":\"Task time overlaps\"}", 406);
        }

        protected void sendInternalError(HttpExchange exchange) throws IOException {
            sendText(exchange, "{\"error\":\"Internal Server Error\"}", 500);
        }

        protected void sendBadRequest(HttpExchange exchange) throws IOException {
            sendText(exchange, "{\"error\":\"Bad Request\"}", 400);
        }

        protected <T> T parseJson(HttpExchange exchange, Class<T> clazz) throws IOException {
            try (InputStream is = exchange.getRequestBody()) {
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                return gson.fromJson(body, clazz);
            } catch (JsonSyntaxException e) {
                sendBadRequest(exchange);
                return null;
            }
        }
    }

    private class TasksHandler extends BaseHttpHandler {
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
                        sendText(exchange, "{\"error\":\"Method Not Allowed\"}", 405);
                }
            } catch (FileBackedTaskManager.ManagerSaveException e) {
                sendHasInteractions(exchange);
            } catch (Exception e) {
                e.printStackTrace();
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
                        sendText(exchange, gson.toJson(task), 200);
                    } else {
                        sendNotFound(exchange);
                    }
                } catch (NumberFormatException e) {
                    sendBadRequest(exchange);
                }
            } else {
                List<Task> tasks = taskManager.getTasks();
                sendText(exchange, gson.toJson(tasks), 200);
            }
        }

        private void handlePost(HttpExchange exchange) throws IOException {
            Task task = parseJson(exchange, Task.class);
            if (task == null) return;

            try {
                if (task.getId() == 0) {
                    int id = taskManager.addTask(task);
                    sendText(exchange, "{\"id\":" + id + "}", 201);
                } else {
                    taskManager.updateTask(task);
                    sendText(exchange, "{\"status\":\"updated\"}", 200);
                }
            } catch (FileBackedTaskManager.ManagerSaveException e) {
                sendHasInteractions(exchange);
            }
        }

        private void handleDelete(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            if (query == null) {
                taskManager.deleteTasks();
                sendText(exchange, "{\"status\":\"all tasks deleted\"}", 200);
            } else {
                try {
                    int id = Integer.parseInt(query.split("=")[1]);
                    taskManager.deleteTaskById(id);
                    sendText(exchange, "{\"status\":\"task deleted\"}", 200);
                } catch (NumberFormatException e) {
                    sendBadRequest(exchange);
                }
            }
        }
    }

    private class SubtasksHandler extends BaseHttpHandler {
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
                        sendText(exchange, "{\"error\":\"Method Not Allowed\"}", 405);
                }
            } catch (FileBackedTaskManager.ManagerSaveException e) {
                sendHasInteractions(exchange);
            } catch (Exception e) {
                e.printStackTrace();
                sendInternalError(exchange);
            }
        }

        private void handleGet(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.matches("/subtasks/\\d+")) {
                try {
                    int id = Integer.parseInt(path.split("/")[2]);
                    Subtask subtask = taskManager.getSubtaskById(id);
                    if (subtask != null) {
                        sendText(exchange, gson.toJson(subtask), 200);
                    } else {
                        sendNotFound(exchange);
                    }
                } catch (NumberFormatException e) {
                    sendBadRequest(exchange);
                }
            } else {
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.startsWith("epic=")) {
                    try {
                        int epicId = Integer.parseInt(query.split("=")[1]);
                        List<Subtask> subtasks = taskManager.getEpicSubtasks(epicId);
                        sendText(exchange, gson.toJson(subtasks), 200);
                    } catch (NumberFormatException e) {
                        sendBadRequest(exchange);
                    }
                } else {
                    List<Subtask> subtasks = taskManager.getSubtasks();
                    sendText(exchange, gson.toJson(subtasks), 200);
                }
            }
        }

        private void handlePost(HttpExchange exchange) throws IOException {
            Subtask subtask = parseJson(exchange, Subtask.class);
            if (subtask == null) return;

            try {
                if (subtask.getId() == 0) {
                    int id = taskManager.addSubtask(subtask);
                    sendText(exchange, "{\"id\":" + id + "}", 201);
                } else {
                    taskManager.updateSubtask(subtask);
                    sendText(exchange, "{\"status\":\"updated\"}", 200);
                }
            } catch (FileBackedTaskManager.ManagerSaveException e) {
                sendHasInteractions(exchange);
            }
        }

        private void handleDelete(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            if (query == null) {
                taskManager.deleteSubtasks();
                sendText(exchange, "{\"status\":\"all subtasks deleted\"}", 200);
            } else {
                try {
                    int id = Integer.parseInt(query.split("=")[1]);
                    taskManager.deleteSubtaskById(id);
                    sendText(exchange, "{\"status\":\"subtask deleted\"}", 200);
                } catch (NumberFormatException e) {
                    sendBadRequest(exchange);
                }
            }
        }
    }

    private class EpicsHandler extends BaseHttpHandler {
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
                        sendText(exchange, "{\"error\":\"Method Not Allowed\"}", 405);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
                        sendText(exchange, gson.toJson(epic), 200);
                    } else {
                        sendNotFound(exchange);
                    }
                } catch (NumberFormatException e) {
                    sendBadRequest(exchange);
                }
            } else {
                List<Epic> epics = taskManager.getEpics();
                sendText(exchange, gson.toJson(epics), 200);
            }
        }

        private void handlePost(HttpExchange exchange) throws IOException {
            Epic epic = parseJson(exchange, Epic.class);
            if (epic == null) return;

            if (epic.getId() == 0) {
                int id = taskManager.addEpic(epic);
                sendText(exchange, "{\"id\":" + id + "}", 201);
            } else {
                taskManager.updateEpic(epic);
                sendText(exchange, "{\"status\":\"updated\"}", 200);
            }
        }

        private void handleDelete(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            if (query == null) {
                taskManager.deleteEpics();
                sendText(exchange, "{\"status\":\"all epics deleted\"}", 200);
            } else {
                try {
                    int id = Integer.parseInt(query.split("=")[1]);
                    taskManager.deleteEpicById(id);
                    sendText(exchange, "{\"status\":\"epic deleted\"}", 200);
                } catch (NumberFormatException e) {
                    sendBadRequest(exchange);
                }
            }
        }
    }

    private class HistoryHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("GET".equals(exchange.getRequestMethod())) {
                    List<Task> history = taskManager.getHistory();
                    sendText(exchange, gson.toJson(history), 200);
                } else {
                    sendText(exchange, "{\"error\":\"Method Not Allowed\"}", 405);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendInternalError(exchange);
            }
        }
    }

    private class PrioritizedHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("GET".equals(exchange.getRequestMethod())) {
                    List<Task> prioritized = taskManager.getPrioritizedTasks();
                    sendText(exchange, gson.toJson(prioritized), 200);
                } else {
                    sendText(exchange, "{\"error\":\"Method Not Allowed\"}", 405);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendInternalError(exchange);
            }
        }
    }
}