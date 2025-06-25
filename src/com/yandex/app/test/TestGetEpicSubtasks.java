package com.yandex.app.test;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestGetEpicSubtasks {
    @Test
    public void testGetEpicSubtasks() {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Эпик", "Описание");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание", epicId);
        taskManager.addSubtask(subtask);

        assertEquals(1, taskManager.getEpicSubtasks(epicId).size());
    }
}
