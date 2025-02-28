package com.yandex.app.test;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EpicSelfSubtaskTest {

    @Test
    public void testEpicCannotBeItsOwnSubtask() {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", epicId);
        subtask.setId(epicId);

        int result = taskManager.addSubtask(subtask);
        assertEquals(-1, result, "Эпик не должен быть своей же подзадачей.");
    }
}