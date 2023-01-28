package ru.yandex.practicum.tasktracker.managers;

import ru.yandex.practicum.tasktracker.managers.historymanagers.HistoryManager;
import ru.yandex.practicum.tasktracker.managers.historymanagers.InMemoryHistoryManager;
import ru.yandex.practicum.tasktracker.managers.taskmanagers.FileBackedTasksManager;
import ru.yandex.practicum.tasktracker.managers.taskmanagers.HttpTaskManager;
import ru.yandex.practicum.tasktracker.managers.taskmanagers.TaskManager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new HttpTaskManager("http://localhost:8078");
    }

    public static TaskManager getFileBackedTasksManager(File file) {
        return new FileBackedTasksManager(file, false);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
