import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEpicIdChangeInSubtask {
    @Test
    public void testEpicIdChangeInSubtask() {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Эпик 1", "Описание");
        Epic epic2 = new Epic("Эпик 2", "Описание");
        int epic1Id = taskManager.addEpic(epic1);
        int epic2Id = taskManager.addEpic(epic2);

        Subtask subtask = new Subtask("Подзадача", "Описание", epic1Id);
        subtask.setEpicId(epic2Id); // Меняем epicId

        assertEquals(epic2Id, subtask.getEpicId()); // Ожидаем новое значение
    }
}

