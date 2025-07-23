package com.yandex.app.service;

import com.yandex.app.model.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder()))
    );
    static int counter = 0;

    HistoryManager imhm = Managers.getDefaultHistory();

    public static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(String message) {
            super(message);
        }
    }

    private int getNewId() {
        return ++counter;
    }

    void addToPrioritized(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private boolean isTimeOverlapping(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return !(end1.isBefore(start2) || end2.isBefore(start1));
    }

    private boolean hasTimeOverlaps(Task newTask) {
        if (newTask.getStartTime() == null) {
            return false;
        }
        return prioritizedTasks.stream()
                .anyMatch(existingTask -> isTimeOverlapping(newTask, existingTask));
    }

    @Override
    public int addTask(Task task) {
        if (hasTimeOverlaps(task)) {
            throw new ManagerSaveException("Задача пересекается по времени с существующей");
        }
        final int id = getNewId();
        task.setId(id);
        tasks.put(id, task);
        addToPrioritized(task);
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

        if (hasTimeOverlaps(subtask)) {
            throw new ManagerSaveException("Подзадача пересекается по времени с существующей");
        }

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask);
        }

        final int id = getNewId();
        subtask.setId(id);
        subtasks.put(id, subtask);
        addToPrioritized(subtask);
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
        Task task = tasks.get(id);
        if (task != null) {
            imhm.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            imhm.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            imhm.add(subtask);
        }
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            prioritizedTasks.remove(tasks.get(task.getId()));
            if (hasTimeOverlaps(task)) {
                throw new ManagerSaveException("Задача пересекается по времени с существующей");
            }
            tasks.put(task.getId(), task);
            addToPrioritized(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            if (hasTimeOverlaps(subtask)) {
                throw new ManagerSaveException("Подзадача пересекается по времени с существующей");
            }
            subtasks.put(subtask.getId(), subtask);
            addToPrioritized(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.updateStatus();
            }
        }
    }

    @Override
    public void deleteTaskById(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        imhm.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                prioritizedTasks.remove(subtask);
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
            prioritizedTasks.remove(subtask);
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
        tasks.keySet().forEach(id -> {
            prioritizedTasks.remove(tasks.get(id));
            imhm.remove(id);
        });
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.values().forEach(epic -> {
            imhm.remove(epic.getId());
            epic.getSubtasks().forEach(subtask -> {
                prioritizedTasks.remove(subtask);
                imhm.remove(subtask.getId());
            });
        });
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.values().forEach(subtask -> {
            prioritizedTasks.remove(subtask);
            imhm.remove(subtask.getId());
        });
        subtasks.clear();

        epics.values().forEach(epic -> {
            epic.getSubtasks().clear();
            epic.updateStatus();
        });
    }

    @Override
    public List<Task> getHistory() {
        return imhm.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
}

