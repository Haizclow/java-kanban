package com.yandex.app.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {
    protected final Gson gson;

    protected BaseHttpHandler(Gson gson) {
        this.gson = gson;
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendSuccess(HttpExchange exchange, String response) throws IOException {
        sendText(exchange, response, HttpURLConnection.HTTP_OK);
    }

    protected void sendCreated(HttpExchange exchange, String response) throws IOException {
        sendText(exchange, response, HttpURLConnection.HTTP_CREATED);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Not Found\"}", HttpURLConnection.HTTP_NOT_FOUND);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Task time overlaps\"}", HttpURLConnection.HTTP_CONFLICT);
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Internal Server Error\"}", HttpURLConnection.HTTP_INTERNAL_ERROR);
    }

    protected void sendBadRequest(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Bad Request\"}", HttpURLConnection.HTTP_BAD_REQUEST);
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Method Not Allowed\"}", HttpURLConnection.HTTP_BAD_METHOD);
    }

    protected <T> T parseJson(HttpExchange exchange, Class<T> clazz) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return gson.fromJson(body, clazz);
        }
    }
}