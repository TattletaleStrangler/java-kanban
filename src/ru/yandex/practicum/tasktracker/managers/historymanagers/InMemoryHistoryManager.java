package ru.yandex.practicum.tasktracker.managers.historymanagers;

import ru.yandex.practicum.tasktracker.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node tail;

    public InMemoryHistoryManager() {
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        Integer id = task.getId();
        removeNode(id);
        linkLast(task);
        nodeMap.put(task.getId(), tail);
    }

    @Override
    public void remove(int id) {
        removeNode(id);
        nodeMap.remove(id);
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
        Node newNode = new Node(tail, task, null);

        if (tail != null) {
            tail.next = newNode;
        }

        tail = newNode;
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

    private void removeNode(int id) {
        Node node = nodeMap.get(id);

        if (node == null) {
            return;
        }

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
