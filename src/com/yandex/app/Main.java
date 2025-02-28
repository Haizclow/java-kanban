
package com.yandex.app;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();


        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        final int epicId1 = taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epicId1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epicId1);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        printAllTasks(taskManager);
        System.out.println("--------------------------------------------------------");
        taskManager.deleteTaskById(task1.getId());
        printAllTasks(taskManager);
    }

    public static void printAllTasks(TaskManager taskManager) {
        System.out.println("Список всех задач:");
        System.out.println("Задачи:");
        List<Task> tasks = taskManager.getTasks();
        for (Task task : tasks) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        List<Epic> epics = taskManager.getEpics();
        for (Epic epic : epics) {
            System.out.println(epic);
        }

        System.out.println("Подзадачи:");
        List<Subtask> subtasks = taskManager.getSubtasks();
        for (Subtask subtask : subtasks) {
            System.out.println(subtask);
        }
    }
}
