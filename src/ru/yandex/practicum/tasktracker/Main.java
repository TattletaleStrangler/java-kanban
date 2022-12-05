package ru.yandex.practicum.tasktracker;

import ru.yandex.practicum.tasktracker.managers.Managers;
import ru.yandex.practicum.tasktracker.managers.taskmanagers.TaskManager;
import ru.yandex.practicum.tasktracker.tasks.Epic;
import ru.yandex.practicum.tasktracker.tasks.Subtask;
import ru.yandex.practicum.tasktracker.tasks.Task;
import ru.yandex.practicum.tasktracker.tasks.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Task task1 = new Task("Таск 1", "Описание таска 1");
        Task task2 = new Task("Таск 2", "Описание таска 2");

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Subtask subtask1 = new Subtask("Подзадача 1 Эпика 1", "Описание подзадачи 1 эпика 1");
        Subtask subtask2 = new Subtask("Подзадача 2 Эпика 1", "Описание подзадачи 2 эпика 1");
        Subtask subtask3 = new Subtask("Подзадача 3 Эпика 1", "Описание подзадачи 3 эпика 1");

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");

        TaskManager manager = Managers.getDefault();
        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);
        manager.addNewSubtask(subtask3);

        epic1.addSubtask(subtask1.getId());
        epic1.addSubtask(subtask2.getId());
        epic1.addSubtask(subtask3.getId());

        manager.addNewEpic(epic1);
        manager.addNewEpic(epic2);

        manager.getEpicById(6);
        manager.getEpicById(7);
        manager.getTaskById(2);
        manager.getSubtaskById(3);
        manager.getSubtaskById(4);
        manager.getTaskById(1);
        manager.getSubtaskById(5);

        List<Task> tasks = manager.getHistory();

        for (Task task : tasks) {
            System.out.print(task.getId());
        }
        System.out.println();

        System.out.println("вызов повторяющихся задач с id = 2, 6");
        manager.getTaskById(2);
        manager.getEpicById(6);
        tasks = manager.getHistory();

        for (Task task : tasks) {
            System.out.print(task.getId());
        }

        System.out.println();
/*
        System.out.println("удаление задачи с id = 1");
        manager.deleteTaskById(1);
        tasks = manager.getHistory();

        for (Task task : tasks) {
            System.out.print(task.getId());
        }

        System.out.println();

        System.out.println("удаление эпика с id = 6 с тремя подзадачами с id = 3, 4, 5");
        manager.deleteEpicById(6);
        tasks = manager.getHistory();

        for (Task task : tasks) {
            System.out.print(task.getId());
        }

        System.out.println();

        manager.getEpicById(3);
        manager.deleteEpicById(3);
        manager.deleteTaskById(15);
*/
        System.out.println("Удаление обычных задач с id 1, 2");
        manager.deleteTasks();
        tasks = manager.getHistory();

        for (Task task : tasks) {
            System.out.print(task.getId());
        }

        System.out.println();

        System.out.println("Удаление эпиков с id 6, 7, соответственно и подзадач c id 3, 4, 5");
        manager.deleteEpics();
        tasks = manager.getHistory();

        for (Task task : tasks) {
            System.out.print(task.getId());
        }

        System.out.println();

    }
}
