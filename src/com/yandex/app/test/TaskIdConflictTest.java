package com.yandex.app.test;

import com.yandex.app.model.Task;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskIdConflictTest {

    @Test
    public void testTaskIdNoConflict() {
        TaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        task1.setId(1);
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2");
        int task2Id = taskManager.addTask(task2);

        assertNotEquals(task1.getId(), task2Id, "ID задач не должны конфликтовать.");
    }
}
