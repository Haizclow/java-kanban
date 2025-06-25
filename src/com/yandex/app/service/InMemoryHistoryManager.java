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

    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

    private List<Task> getTasks() {
        List<Task> result = new ArrayList<>();
        Node current = head;
        while (current != null) {
            result.add(current.task);
            current = current.next;
        }
        return result;
    }

    private void linkLast(Task task) {
        Node newNode = new Node(task, tail, null);
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
    }

    private void removeNode(Node node) {
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) return;

        int id = task.getId();
        // Внутренняя логика удаления без вызова API метода remove()
        Node existingNode = historyMap.get(id);
        if (existingNode != null) {
            removeNode(existingNode);
        }

        linkLast(task);
        historyMap.put(id, tail);
    }

    @Override
    public void remove(int id) {
        Node node = historyMap.remove(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
