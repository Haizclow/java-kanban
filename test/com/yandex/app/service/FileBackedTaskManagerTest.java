package com.yandex.app.service;

import com.yandex.app.model.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File testFile;

    @BeforeEach
    void setUp() {
        testFile = new File("test_tasks.csv");
        manager = new FileBackedTaskManager(testFile);
    }

    @AfterEach
    void tearDown() {
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Override
    protected FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(testFile);
    }

    @Test
    void shouldSaveAndLoadEmptyManager() {
        manager.save();
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(testFile);
        assertTrue(loaded.getTasks().isEmpty());
        assertTrue(loaded.getEpics().isEmpty());
    }

    @Test
    void shouldSaveAndLoadTasks() {
        Task task = new Task("Task", "Desc", LocalDateTime.now(), 30);
        manager.addTask(task);
        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(testFile);
        assertEquals(1, loaded.getTasks().size());
        assertEquals("Task", loaded.getTaskById(task.getId()).getTitle());
    }

    @Test
    void shouldThrowExceptionWhenFileInvalid() {
        File invalid = new File("/invalid/path/tasks.csv");
        assertThrows(FileBackedTaskManager.ManagerSaveException.class, () -> {
            // Попытка создать менеджер с несуществующим путем
            new FileBackedTaskManager(invalid).save(); // Явно вызываем save для проверки
        });
    }
}