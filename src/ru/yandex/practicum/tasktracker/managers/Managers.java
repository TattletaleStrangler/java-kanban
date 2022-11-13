package ru.yandex.practicum.tasktracker.managers;

import ru.yandex.practicum.tasktracker.managers.historymanagers.HistoryManager;
import ru.yandex.practicum.tasktracker.managers.historymanagers.InMemoryHistoryManager;
import ru.yandex.practicum.tasktracker.managers.taskmanagers.InMemoryTaskManager;
import ru.yandex.practicum.tasktracker.managers.taskmanagers.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
