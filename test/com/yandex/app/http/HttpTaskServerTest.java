package com.yandex.app.http;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Task;
import com.yandex.app.service.Managers;
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
        Thread.sleep(500);
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }

    @Test
    void testCreateAndGetTask() throws Exception {
        String taskJson = """
                {
                    "title": "Test Task",
                    "description": "Test Description",
                    "status": "NEW"
                }
                """;

        HttpResponse<String> postResponse = sendPostRequest("/tasks", taskJson);
        assertEquals(201, postResponse.statusCode());
        assertTrue(postResponse.body().contains("\"id\":"));

        HttpResponse<String> getResponse = sendGetRequest("/tasks");
        assertEquals(200, getResponse.statusCode());
        assertTrue(getResponse.body().contains("Test Task"));
    }

    @Test
    void testGetTaskById() throws Exception {
        Task task = new Task("Test", "Description");
        int taskId = server.taskManager.addTask(task);

        HttpResponse<String> response = sendGetRequest("/tasks/" + taskId);
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Test"));
    }

    @Test
    void testUpdateTask() throws Exception {
        Task task = new Task("Original", "Desc");
        int taskId = server.taskManager.addTask(task);

        String updateJson = """
                {
                    "id": %d,
                    "title": "Updated",
                    "description": "New Desc",
                    "status": "IN_PROGRESS"
                }
                """.formatted(taskId);

        HttpResponse<String> putResponse = sendPostRequest("/tasks", updateJson);
        assertEquals(200, putResponse.statusCode());
        assertTrue(putResponse.body().contains("updated"));

        HttpResponse<String> getResponse = sendGetRequest("/tasks/" + taskId);
        assertTrue(getResponse.body().contains("Updated"));
        assertTrue(getResponse.body().contains("IN_PROGRESS"));
    }

    @Test
    void testDeleteTask() throws Exception {

        Task task = new Task("To delete", "Desc");
        int taskId = server.taskManager.addTask(task);


        HttpResponse<String> deleteResponse = sendDeleteRequest("/tasks?id=" + taskId);
        assertEquals(200, deleteResponse.statusCode());


        HttpResponse<String> getResponse = sendGetRequest("/tasks/" + taskId);
        assertEquals(404, getResponse.statusCode());
    }

//    @Test
//    void testCreateAndGetEpic() throws Exception {
//
//        assertEquals(0, server.taskManager.getEpics().size());
//
//        String epicJson = """
//            {
//                "title": "Test Epic",
//                "description": "Epic Description",
//                "status": "NEW"
//            }
//            """;
//
//        HttpResponse<String> postResponse = sendPostRequest("/epics", epicJson);
//        assertEquals(201, postResponse.statusCode(),
//                "Failed to create epic. Response: " + postResponse.body());
//
//        assertEquals(1, server.taskManager.getEpics().size());
//
//        HttpResponse<String> getResponse = sendGetRequest("/epics");
//        assertEquals(200, getResponse.statusCode());
//        assertTrue(getResponse.body().contains("Test Epic"),
//                "Epic not found in response: " + getResponse.body());
//    }
//
//    @Test
//    void testCreateSubtaskWithEpic() throws Exception {
//
//        Epic epic = new Epic("Parent Epic", "Desc");
//        int epicId = server.taskManager.addEpic(epic);
//
//        String subtaskJson = """
//            {
//                "title": "Subtask",
//                "description": "Sub Desc",
//                "epicId": %d,
//                "startTime": "2023-01-01T10:00:00",
//                "duration": 60
//            }
//            """.formatted(epicId);
//
//        HttpResponse<String> postResponse = sendPostRequest("/subtasks", subtaskJson);
//        assertEquals(201, postResponse.statusCode());
//
//        HttpResponse<String> getResponse = sendGetRequest("/subtasks?epic=" + epicId);
//        assertEquals(200, getResponse.statusCode());
//        assertTrue(getResponse.body().contains("Subtask"));
//    }

    @Test
    void testHistory() throws Exception {

        Task task1 = new Task("Task 1", "Desc");
        Task task2 = new Task("Task 2", "Desc");
        int id1 = server.taskManager.addTask(task1);
        int id2 = server.taskManager.addTask(task2);

        server.taskManager.getTaskById(id1);
        server.taskManager.getTaskById(id2);


        HttpResponse<String> response = sendGetRequest("/history");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task 1"));
        assertTrue(response.body().contains("Task 2"));
    }

    @Test
    void testPrioritizedTasks() throws Exception {

        Task task1 = new Task("First", "Desc", LocalDateTime.now().plusHours(1), 30);
        Task task2 = new Task("Second", "Desc", LocalDateTime.now(), 30);
        server.taskManager.addTask(task1);
        server.taskManager.addTask(task2);

        HttpResponse<String> response = sendGetRequest("/prioritized");
        assertEquals(200, response.statusCode());

        int index1 = response.body().indexOf("Second");
        int index2 = response.body().indexOf("First");
        assertTrue(index1 < index2);
    }


    @Test
    void testInvalidMethod() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
    }

    @Test
    void testNotFound() throws Exception {
        HttpResponse<String> response = sendGetRequest("/tasks/9999");
        assertEquals(404, response.statusCode());
    }

    // Вспомогательные методы
    private HttpResponse<String> sendGetRequest(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPostRequest(String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDeleteRequest(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .DELETE()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}