package com.yandex.app.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import com.yandex.app.http.adapter.DurationAdapter;
import com.yandex.app.http.adapter.LocalDateTimeAdapter;
import com.yandex.app.http.handler.*;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private final HttpServer server;
    public final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        this.server = HttpServer.create(new InetSocketAddress(8080), 0);
        registerHandlers(gson);
    }

    private void registerHandlers(Gson gson) {
        server.createContext("/tasks", new TasksHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
        server.createContext("/epics", new EpicsHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public void start() {
        server.start();
        System.out.println("HTTP Task Server started on port 8080");
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP Task Server stopped");
    }
}