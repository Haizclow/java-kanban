package com.yandex.app.service;

import com.yandex.app.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void shouldHandleTaskDeletion() {
        Task task = new Task("Task", "Desc");
        int id = manager.addTask(task);
        manager.deleteTaskById(id);
        assertTrue(manager.getTasks().isEmpty());
    }
}