package ru.yandex.practicum.tasktracker;

import ru.yandex.practicum.tasktracker.managers.Managers;
import ru.yandex.practicum.tasktracker.managers.taskmanagers.FileBackedTasksManager;
import ru.yandex.practicum.tasktracker.managers.taskmanagers.TaskManager;
import ru.yandex.practicum.tasktracker.tasks.Epic;
import ru.yandex.practicum.tasktracker.tasks.Subtask;
import ru.yandex.practicum.tasktracker.tasks.Task;
import ru.yandex.practicum.tasktracker.tasks.TaskStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = FileBackedTasksManager.loadFromFile(new File(System.getProperty("user.home") + "\\backup.csv"));
        Task task2 = new Task("Таск 2", "Описание таска 2");
        manager.addNewTask(task2);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        manager.addNewEpic(epic2);
        Subtask subtask2 = new Subtask("Подзадача 2 Эпика 1", "Описание подзадачи 2 эпика 1", epic2.getId());
        Subtask subtask3 = new Subtask("Подзадача 3 Эпика 1", "Описание подзадачи 3 эпика 1", epic2.getId(), TaskStatus.IN_PROGRESS);
        manager.addNewSubtask(subtask2);
        manager.addNewSubtask(subtask3);

        manager.getTaskById(1);
        manager.getSubtaskById(3);
        manager.getEpicById(2);
        manager.getSubtaskById(4);

        //повторяющиеся:

        manager.getTaskById(1);
        manager.getEpicById(2);

        manager.deleteTaskById(1);
        manager.deleteEpicById(2);
    }
}
