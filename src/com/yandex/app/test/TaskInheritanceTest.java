package com.yandex.app.test;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskInheritanceTest {

    @Test
    public void testSubtaskEqualityById() {
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", 1);
        subtask1.setId(2);

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", 1);
        subtask2.setId(2);

        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым id должны быть равны.");
    }

    @Test
    public void testEpicEqualityById() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        epic1.setId(3);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        epic2.setId(3);

        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны.");
    }
}