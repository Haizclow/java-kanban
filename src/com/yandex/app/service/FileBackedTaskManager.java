package com.yandex.app.service;

import com.yandex.app.model.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            if (!file.exists()) {
                return manager;
            }

            final String content = Files.readString(file.toPath());
            final String[] lines = content.split("\n");

            if (lines.length < 2) { // Пустой файл или только заголовок
                return manager;
            }

            // Чтение задач
            for (int i = 1; i < lines.length; i++) {
                final Task task = manager.fromString(lines[i]);
                if (task != null) {
                    if (task instanceof Epic) {
                        manager.epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        manager.subtasks.put(task.getId(), (Subtask) task);
                    } else {
                        manager.tasks.put(task.getId(), task);
                    }
                }
            }

            // Восстановление связей для подзадач
            for (Subtask subtask : manager.subtasks.values()) {
                final Epic epic = manager.epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubtask(subtask);
                }
            }

            // Восстановление счетчика ID
            int maxId = manager.tasks.keySet().stream().max(Integer::compare).orElse(0);
            maxId = Math.max(maxId, manager.epics.keySet().stream().max(Integer::compare).orElse(0));
            maxId = Math.max(maxId, manager.subtasks.keySet().stream().max(Integer::compare).orElse(0));
            manager.counter = maxId;

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }

        return manager;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");

            // Сохраняем все задачи в порядке: обычные, эпики, подзадачи
            for (Task task : tasks.values()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : epics.values()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }

    private String toString(Task task) {
        TaskType type = task instanceof Epic ? TaskType.EPIC :
                task instanceof Subtask ? TaskType.SUBTASK : TaskType.TASK;

        String epicId = task instanceof Subtask ?
                String.valueOf(((Subtask) task).getEpicId()) : "";

        return String.join(",",
                String.valueOf(task.getId()),
                type.name(),
                task.getTitle(),
                task.getStatus().name(),
                task.getDescription(),
                epicId);
    }

    private Task fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        String[] parts = value.split(",");
        if (parts.length < 6) {
            return null;
        }

        try {
            int id = Integer.parseInt(parts[0]);
            TaskType type = TaskType.valueOf(parts[1]);
            String name = parts[2];
            TaskStatus status = TaskStatus.valueOf(parts[3]);
            String description = parts[4];

            switch (type) {
                case TASK:
                    Task task = new Task(name, description);
                    task.setId(id);
                    task.setStatus(status);
                    return task;
                case EPIC:
                    Epic epic = new Epic(name, description);
                    epic.setId(id);
                    epic.setStatus(status);
                    return epic;
                case SUBTASK:
                    int epicId = Integer.parseInt(parts[5]);
                    Subtask subtask = new Subtask(name, description, epicId);
                    subtask.setId(id);
                    subtask.setStatus(status);
                    return subtask;
                default:
                    return null;
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // Переопределенные методы с автосохранением
    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    public static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}