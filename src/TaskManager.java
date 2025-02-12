import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task > tasks = new HashMap<>();
    HashMap<Integer, Subtask > subtasks = new HashMap<>();
    HashMap<Integer, Epic > epics = new HashMap<>();
    private static int counter = 0;

    // Метод для получения нового идентификатора
    public int getNewId() {
        return ++counter; // Увеличиваем счётчик и возвращаем новое значение
    }


    public void addTask(Task task){
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic){
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask);
        }
    }

    public void printAllTasks() {
        System.out.println("Список всех задач:");
        System.out.println("Задачи: " + tasks.values());
        System.out.println("Эпики: " + epics.values());
        System.out.println("Подзадачи: " + subtasks.values());
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }


    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
        }
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtasks().remove(subtask);
            }
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public void updateTaskStatus(int taskId, TaskStatus status) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.setStatus(status);
        }
    }

    public void updateSubtaskStatus(int subtaskId, TaskStatus status) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            subtask.setStatus(status);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.updateStatus(); // Обновляем статус эпика при изменении статуса подзадачи
            }
        }
    }
}




