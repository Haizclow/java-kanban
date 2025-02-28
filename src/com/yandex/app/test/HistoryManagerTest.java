package com.yandex.app.test;

import com.yandex.app.model.Task;
import com.yandex.app.service.HistoryManager;
import com.yandex.app.service.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {

    @Test
    public void testHistoryManagerPreservesTaskState() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Задача 1", "Описание задачи 1");
        task.setId(1);

        historyManager.add(task);
        task.setTitle("Новое название");

        Task savedTask = historyManager.getHistory().get(0);
        assertEquals("Задача 1", savedTask.getTitle(), "История должна сохранять предыдущую версию задачи.");
    }
}