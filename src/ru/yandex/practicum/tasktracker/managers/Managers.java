package ru.yandex.practicum.tasktracker.managers;

import ru.yandex.practicum.tasktracker.managers.history_managers.HistoryManager;
import ru.yandex.practicum.tasktracker.managers.history_managers.InMemoryHistoryManager;
import ru.yandex.practicum.tasktracker.managers.task_managers.InMemoryTaskManager;
import ru.yandex.practicum.tasktracker.managers.task_managers.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
