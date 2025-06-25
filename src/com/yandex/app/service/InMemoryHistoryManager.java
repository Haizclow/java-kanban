package com.yandex.app.service;

import com.yandex.app.model.Task;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }
//    private static final int MAX_SIZE = 10;
//
//    private final List<Task> history = new ArrayList<>(MAX_SIZE);

    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (task == null) return;

        int id = task.getId();
        remove(id);//удалить старую версию задачи
        linkLast(task);//добавить задачу в конец
        historyMap.put(id, tail);
    }

    private void linkLast(Task task) {
        Node newNode = new Node(task, tail, null);//создаю новый узел
        if (tail == null){
            head = newNode; //это если список пуст, то новый узел станет головой
        } else {
            tail.next = newNode;//иначе я связываю старый тэйл с новым узлом
        }
        tail = newNode; // и обновляю тейл
    }

    private void removeNode(Node node) {
        if (node == null) return;

        Node prev = node.prev;
        Node next = node.next;

        if (prev == null) {
            head = next;  // Удаляем первый элемент
        } else {
            prev.next = next;
        }

        if (next == null) {
            tail = prev;  // Удаляем последний элемент
        } else {
            next.prev = prev;
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node current = head;

        while (current != null) {
            result.add(current.task);
            current = current.next;
        }

        return result;
    }

    @Override
    public void remove(int id) {
        Node node = historyMap.get(id);
        if (node != null) {
            removeNode(node);
            historyMap.remove(id);
        }
    }
}

//я очень долго не кодил я чувствую себя тупым;(