package ru.yandex.practicum.tasktracker.managers.historymanagers;

import ru.yandex.practicum.tasktracker.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history;
    private Node tail;

    public InMemoryHistoryManager() {
        history = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = history.remove(id);
        if (node != null) removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasksHistory = getTasks();
        return tasksHistory;
    }

    private static class Node {
        Node prev;
        Node next;
        Task task;

        public Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }

    private void linkLast(Task task) {
        Node oldNode = history.get(task.getId());

        if (oldNode != null) removeNode(oldNode);

        Node newNode = new Node(tail, task, null);

        if (tail != null) {
            tail.next = newNode;
        }

        tail = newNode;

        history.put(task.getId(), newNode);
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node currentNode = tail;

        while (currentNode != null) {
            tasks.add(currentNode.task);
            currentNode = currentNode.prev;
        }

        return tasks;
    }

    private void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;

        if (prev != null) {
            prev.next = next;
        }

        if (next != null) {
            next.prev = prev;
        } else {
            tail = prev;
        }
    }
}
