package com.yandex.app.test;

import com.yandex.app.model.Task;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestIdChangeAfterAdding {
    @Test
    public void testIdChangeAfterAdding() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Задача", "Описание");
        int taskId = taskManager.addTask(task);

        task.setId(999); // Попытка изменить id

        assertNotNull(taskManager.getTaskById(taskId)); // Менеджер должен работать со старым id
        assertNull(taskManager.getTaskById(999)); // Новый id не должен находиться
    }
}
