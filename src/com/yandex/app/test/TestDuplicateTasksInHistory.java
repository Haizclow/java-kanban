package com.yandex.app.test;

import com.yandex.app.model.Task;
import com.yandex.app.service.HistoryManager;
import com.yandex.app.service.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDuplicateTasksInHistory {
    @Test
    public void testDuplicateTasksInHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Задача", "Описание");
        task.setId(1);

        historyManager.add(task);
        historyManager.add(task); // Дубликат

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size()); // Должна остаться одна запись
    }
}
