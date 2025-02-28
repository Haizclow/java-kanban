package com.yandex.app.service;

import com.yandex.app.model.Task;


import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_SIZE = 10;

    private final List<Task> history = new ArrayList<>(MAX_SIZE);

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        Task taskCopy = new Task(task.getTitle(), task.getDescription());
        taskCopy.setId(task.getId());
        taskCopy.setStatus(task.getStatus());

        if (history.size() >= MAX_SIZE) {
            history.remove(0);
        }
        history.add(taskCopy);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
