package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private static int counter = 0;

    HistoryManager imhm = Managers.getDefaultHistory();


    private int getNewId() {
        return ++counter;
    }

    @Override
    public int addTask(Task task) {
        final int id = getNewId();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        final int id = getNewId();
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (subtask.getEpicId() == subtask.getId()) {
            return -1;
        }

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask);
        }

        final int id = getNewId();
        subtask.setId(id);
        subtasks.put(id, subtask);

        return id;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            return new ArrayList<>(epic.getSubtasks());
        }
        return new ArrayList<>();
    }

    @Override
    public Task getTaskById(int id) {
        imhm.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        imhm.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        imhm.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.updateStatus();
            }
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        imhm.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
                imhm.remove(subtask.getId());
            }
        }
        imhm.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtasks().remove(subtask);
                epic.updateStatus();
            }
            imhm.remove(id);
        }
    }
    @Override
    public void deleteTasks() {
        for (Integer taskId : tasks.keySet()) {
            imhm.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (Epic epic : epics.values()) {
            imhm.remove(epic.getId());
            for (Subtask subtask : epic.getSubtasks()) {
                imhm.remove(subtask.getId());
            }
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            imhm.remove(subtask.getId());
        }
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.updateStatus();
        }
    }

    @Override
    public List<Task> getHistory() {
        return imhm.getHistory();
    }

}