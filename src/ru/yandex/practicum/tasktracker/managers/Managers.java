package ru.yandex.practicum.tasktracker.managers;

import ru.yandex.practicum.tasktracker.managers.historymanagers.HistoryManager;
import ru.yandex.practicum.tasktracker.managers.historymanagers.InMemoryHistoryManager;
import ru.yandex.practicum.tasktracker.managers.taskmanagers.FileBackedTasksManager;
import ru.yandex.practicum.tasktracker.managers.taskmanagers.TaskManager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTasksManager(new File(System.getProperty("user.home") + "\\backup.csv"), false);
    }

    public static TaskManager getFileBackedTasksManager(File file) {
        return new FileBackedTasksManager(file, false);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
