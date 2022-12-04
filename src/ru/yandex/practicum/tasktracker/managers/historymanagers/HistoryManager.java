package ru.yandex.practicum.tasktracker.managers.historymanagers;

import ru.yandex.practicum.tasktracker.tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
