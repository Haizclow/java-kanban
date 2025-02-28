package com.yandex.app.test;

import com.yandex.app.model.Task;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskImmutabilityTest {

    @Test
    public void testTaskImmutability() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Задача 1", "Описание задачи 1");
        int taskId = taskManager.addTask(task);

        Task savedTask = taskManager.getTaskById(taskId);
        assertEquals(task.getTitle(), savedTask.getTitle(), "Название задачи изменилось.");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Описание задачи изменилось.");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Статус задачи изменился.");
    }
}
