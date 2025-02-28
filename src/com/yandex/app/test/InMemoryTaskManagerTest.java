package com.yandex.app.test;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    @Test
    public void testAddAndFindTasks() {
        TaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task("Задача 1", "Описание задачи 1");
        int taskId = taskManager.addTask(task);

        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", epicId);
        int subtaskId = taskManager.addSubtask(subtask);

        assertNotNull(taskManager.getTaskById(taskId), "Задача не найдена.");
        assertNotNull(taskManager.getEpicById(epicId), "Эпик не найден.");
        assertNotNull(taskManager.getSubtaskById(subtaskId), "Подзадача не найдена.");
    }
}
