import com.yandex.app.model.Task;
import com.yandex.app.service.HistoryManager;
import com.yandex.app.service.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRemoveFromHistory {
    @Test
    public void testRemoveFromHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Задача", "Описание");
        task.setId(1);

        historyManager.add(task);
        historyManager.remove(1);

        assertTrue(historyManager.getHistory().isEmpty());
    }
}
