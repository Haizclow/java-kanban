package com.yandex.app.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void shouldReturnNewStatusWhenNoSubtasks() {
        Epic epic = new Epic("Epic", "Description");
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void shouldReturnNewStatusWhenAllSubtasksNew() {
        Epic epic = new Epic("Epic", "Description");
        Subtask sub1 = new Subtask("Sub1", "Desc", epic.getId());
        Subtask sub2 = new Subtask("Sub2", "Desc", epic.getId());
        epic.addSubtask(sub1);
        epic.addSubtask(sub2);
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void shouldReturnDoneStatusWhenAllSubtasksDone() {
        Epic epic = new Epic("Epic", "Description");
        Subtask sub1 = new Subtask("Sub1", "Desc", epic.getId());
        sub1.setStatus(TaskStatus.DONE);
        epic.addSubtask(sub1);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void shouldReturnInProgressStatusWhenMixedStatuses() {
        Epic epic = new Epic("Epic", "Description");
        Subtask sub1 = new Subtask("Sub1", "Desc", epic.getId());
        Subtask sub2 = new Subtask("Sub2", "Desc", epic.getId());
        sub1.setStatus(TaskStatus.NEW);
        sub2.setStatus(TaskStatus.DONE);
        epic.addSubtask(sub1);
        epic.addSubtask(sub2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldReturnInProgressStatusWhenSubtaskInProgress() {
        Epic epic = new Epic("Epic", "Description");
        Subtask sub = new Subtask("Sub", "Desc", epic.getId());
        sub.setStatus(TaskStatus.IN_PROGRESS);
        epic.addSubtask(sub);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }
}