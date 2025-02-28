package com.yandex.app.test;

import com.yandex.app.model.Subtask;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SubtaskSelfEpicTest {

    @Test
    public void testSubtaskCannotBeItsOwnEpic() {
        TaskManager taskManager = new InMemoryTaskManager();
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", 1);
        subtask.setId(1);

        int result = taskManager.addSubtask(subtask);
        assertEquals(-1, result, "Подзадача не должна быть своим же эпиком.");
    }
}