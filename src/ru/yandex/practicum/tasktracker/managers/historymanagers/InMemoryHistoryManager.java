package ru.yandex.practicum.tasktracker.managers.historymanagers;

import ru.yandex.practicum.tasktracker.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final static int MAX_SIZE = 10;
    private final LinkedList<Task> history;

    public InMemoryHistoryManager() {
        history = new LinkedList<>();
    }

    @Override
    public void add(Task task) {
        history.addFirst(task);
        if (history.size() > MAX_SIZE) {
            history.removeLast();
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasksHistory = new ArrayList<>(history);
        Collections.reverse(tasksHistory);
        return tasksHistory;
    }
}
