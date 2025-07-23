package com.yandex.app.service;

import com.yandex.app.model.Task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private final HistoryManager manager = new InMemoryHistoryManager();

    @Test
    void shouldReturnEmptyHistoryInitially() {
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void shouldAddTaskToHistory() {
        Task task = new Task("Task", "Desc");
        task.setId(1);
        manager.add(task);
        assertEquals(1, manager.getHistory().size());
    }

    @Test
    void shouldNotDuplicateTasks() {
        Task task = new Task("Task", "Desc");
        task.setId(1);
        manager.add(task);
        manager.add(task);
        assertEquals(1, manager.getHistory().size());
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        Task task1 = new Task("Task1", "Desc");
        Task task2 = new Task("Task2", "Desc");
        task1.setId(1);
        task2.setId(2);

        manager.add(task1);
        manager.add(task2);
        manager.remove(1);

        assertEquals(1, manager.getHistory().size());
        assertEquals(task2, manager.getHistory().get(0));
    }

    @Test
    void shouldClearHistoryWhenRemoveAllTasks() {
        Task task1 = new Task("Task1", "Desc");
        Task task2 = new Task("Task2", "Desc");
        task1.setId(1);
        task2.setId(2);

        manager.add(task1);
        manager.add(task2);
        manager.remove(1);
        manager.remove(2);

        assertTrue(manager.getHistory().isEmpty());
    }
}