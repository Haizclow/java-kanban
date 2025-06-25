package com.yandex.app.test;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSubtaskRemovalFromEpic {
    @Test
    public void testSubtaskRemovalFromEpic() {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Эпик", "Описание");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание", epicId);
        int subtaskId = taskManager.addSubtask(subtask);

        taskManager.deleteSubtaskById(subtaskId);
        assertTrue(epic.getSubtasks().isEmpty()); // Подзадача должна удалиться из эпика
    }
}
