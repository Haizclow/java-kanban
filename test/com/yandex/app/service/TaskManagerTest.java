package com.yandex.app.service;

import com.yandex.app.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    protected abstract T createManager();

    @BeforeEach
    void setUp() {
        manager = createManager();
    }

    @Test
    void shouldPrioritizeTasksByStartTime() {
        LocalDateTime early = LocalDateTime.now();
        LocalDateTime late = early.plusHours(1);

        Task task2 = new Task("Late", "Desc", late, 30);
        Task task1 = new Task("Early", "Desc", early, 30);

        manager.addTask(task2);
        manager.addTask(task1);

        var prioritized = manager.getPrioritizedTasks();
        assertEquals(early, prioritized.get(0).getStartTime());
        assertEquals(late, prioritized.get(1).getStartTime());
    }

    @Test
    void shouldDetectTimeOverlaps() {
        LocalDateTime now = LocalDateTime.now();
        manager.addTask(new Task("Task1", "Desc", now, 60));

        Task overlappingTask = new Task("Task2", "Desc", now.plusMinutes(30), 30);
        assertThrows(RuntimeException.class, () -> manager.addTask(overlappingTask));
    }

    @Test
    void shouldAllowNonOverlappingTasks() {
        LocalDateTime now = LocalDateTime.now();
        manager.addTask(new Task("Task1", "Desc", now, 30));

        Task nonOverlapping = new Task("Task2", "Desc", now.plusHours(1), 30);
        assertDoesNotThrow(() -> manager.addTask(nonOverlapping));
    }
}
