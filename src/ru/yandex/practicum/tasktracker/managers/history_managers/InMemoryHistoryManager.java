package ru.yandex.practicum.tasktracker.managers.history_managers;

import ru.yandex.practicum.tasktracker.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final static int MAX_SIZE = 10;
    private final List<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        history.add(0, task);
        if (history.size() > MAX_SIZE) {
            history.remove(MAX_SIZE);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasksHistory = new ArrayList<>(history);
        Collections.reverse(tasksHistory);
        return tasksHistory;
    }
}
