
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

        Task task1 = new Task("Задача 1", "Описание 1");
        int taskId1 = taskManager.addTask(task1);

        // Добавляем задачу в историю
        taskManager.getTaskById(taskId1);
        System.out.println("История после добавления: " + taskManager.getHistory());

        // Удаляем задачу
        taskManager.deleteTaskById(taskId1);
        System.out.println("История после удаления: " + taskManager.getHistory());
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
