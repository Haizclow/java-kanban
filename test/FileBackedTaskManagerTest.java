//package com.yandex.app.service;
//
//import com.yandex.app.model.*;
//import org.junit.jupiter.api.*;
//import java.io.*;
//import java.nio.file.Files;
//import static org.junit.jupiter.api.Assertions.*;
//
//class FileBackedTaskManagerTest {
//    private File tempFile;
//    private FileBackedTaskManager manager;
//
//    @BeforeEach
//    void setUp() throws IOException {
//        tempFile = Files.createTempFile("tasks", ".csv").toFile();
//        manager = new FileBackedTaskManager(tempFile);
//    }
//
//    @AfterEach
//    void tearDown() {
//        if (tempFile.exists()) {
//            tempFile.delete();
//        }
//    }
//
//    @Test
//    void shouldSaveAndLoadEmptyManager() {
//        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
//        assertTrue(loaded.getTasks().isEmpty());
//        assertTrue(loaded.getEpics().isEmpty());
//        assertTrue(loaded.getSubtasks().isEmpty());
//    }
//
//    @Test
//    void shouldSaveAndLoadTasks() {
//        Task task = new Task("Task 1", "Description");
//        task.setStatus(TaskStatus.IN_PROGRESS);
//        int taskId = manager.addTask(task);
//
//        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
//        Task loadedTask = loaded.getTaskById(taskId);
//
//        assertNotNull(loadedTask, "Задача не должна быть null");
//        assertEquals(task.getTitle(), loadedTask.getTitle());
//        assertEquals(task.getDescription(), loadedTask.getDescription());
//        assertEquals(task.getStatus(), loadedTask.getStatus());
//    }
//
//    @Test
//    void shouldSaveAndLoadEpicWithSubtasks() {
//        Epic epic = new Epic("Epic 1", "Description");
//        int epicId = manager.addEpic(epic);
//
//        Subtask subtask = new Subtask("Subtask 1", "Description", epicId);
//        subtask.setStatus(TaskStatus.DONE);
//        int subtaskId = manager.addSubtask(subtask);
//
//        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
//
//        Epic loadedEpic = loaded.getEpicById(epicId);
//        Subtask loadedSubtask = loaded.getSubtaskById(subtaskId);
//
//        assertNotNull(loadedEpic, "Эпик не должен быть null");
//        assertNotNull(loadedSubtask, "Подзадача не должна быть null");
//        assertEquals(1, loadedEpic.getSubtasks().size());
//        assertEquals(epicId, loadedSubtask.getEpicId());
//        assertEquals(TaskStatus.DONE, loadedSubtask.getStatus());
//        assertEquals(TaskStatus.DONE, loadedEpic.getStatus());
//    }
//
//    @Test
//    void shouldSaveStatusesCorrectly() {
//        Task task = new Task("Task", "Desc");
//        task.setStatus(TaskStatus.DONE);
//        int taskId = manager.addTask(task);
//
//        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
//        Task loadedTask = loaded.getTaskById(taskId);
//
//        assertNotNull(loadedTask, "Задача не должна быть null");
//        assertEquals(TaskStatus.DONE, loadedTask.getStatus());
//    }
//
//    @Test
//    void shouldHandleInvalidFile() {
//        File invalidFile = new File("/invalid/path/tasks.csv");
//        assertThrows(FileBackedTaskManager.ManagerSaveException.class, () ->
//                new FileBackedTaskManager(invalidFile).save());
//    }
//}