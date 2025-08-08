package com.yandex.app.service;

import com.yandex.app.model.Task;
import org.junit.jupiter.api.*;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.URI;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private HttpTaskServer server;
    private final HttpClient client = HttpClient.newHttpClient();
    private final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    void startServer() throws Exception {
        server = new HttpTaskServer(Managers.getDefault());
        server.start();
        // Даем серверу время на запуск
        Thread.sleep(500);
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }

    @Test
    void testCreateAndGetTask() throws Exception {
        // Тест на создание задачи
        String taskJson = """
                {
                    "title": "Test Task",
                    "description": "Test Description",
                    "status": "NEW"
                }
                """;

        // Отправляем POST-запрос
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode(), "POST request failed");

        // Отправляем GET-запрос для проверки
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "GET request failed");
        assertTrue(getResponse.body().contains("Test Task"), "Task not found in response");
    }

    @Test
    void testCreateTaskWithTime() throws Exception {
        String taskJson = """
                {
                    "title": "Task with time",
                    "description": "Description",
                    "startTime": "2023-01-01T10:00:00",
                    "duration": 60
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("\"id\":"));
    }

    @Test
    void testGetPrioritizedTasks() throws Exception {
        // Добавляем две задачи с разным временем
        Task task1 = new Task("Task 1", "Desc 1", LocalDateTime.now(), 30);
        Task task2 = new Task("Task 2", "Desc 2", LocalDateTime.now().plusHours(1), 30);
        server.taskManager.addTask(task1);
        server.taskManager.addTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task 1"));
        assertTrue(response.body().contains("Task 2"));
    }

    @Test
    void testInvalidRequest() throws Exception {
        // Неправильный JSON
        String invalidJson = "{invalid}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(invalidJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }
}