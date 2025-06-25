import com.yandex.app.model.Task;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDeleteAllTasks {
    @Test
    public void testDeleteAllTasks() {
        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.addTask(new Task("Задача", "Описание"));
        taskManager.deleteTasks();

        assertTrue(taskManager.getTasks().isEmpty());
        assertTrue(taskManager.getHistory().isEmpty()); // История тоже должна очиститься
    }
}
