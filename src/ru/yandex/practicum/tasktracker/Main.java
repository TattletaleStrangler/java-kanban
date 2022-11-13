package ru.yandex.practicum.tasktracker;

import ru.yandex.practicum.tasktracker.managers.Managers;
import ru.yandex.practicum.tasktracker.managers.taskmanagers.TaskManager;
import ru.yandex.practicum.tasktracker.tasks.Epic;
import ru.yandex.practicum.tasktracker.tasks.Subtask;
import ru.yandex.practicum.tasktracker.tasks.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Subtask subtask1 = new Subtask("Подзадача 1 Эпика 1", "Описание подзадачи 1 эпика 1");
        Subtask subtask2 = new Subtask("Подзадача 2 Эпика 1", "Описание подзадачи 2 эпика 1");


        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        Subtask subtask3 = new Subtask("Подзадача 1 Эпика 2", "Описание подзадачи 1 эпика 2");


        TaskManager manager = Managers.getDefault();
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);
        manager.addNewSubtask(subtask3);

        epic1.addSubtask(subtask1.getId());
        epic1.addSubtask(subtask2.getId());
        epic2.addSubtask(subtask3.getId());

        manager.addNewEpic(epic1);
        manager.addNewEpic(epic2);

        System.out.println("Печать эпиков до изменений:");
        System.out.println(epic1);
        System.out.println(epic2);
        System.out.println();

//        System.out.println("Изменен статус подзадачи с id=3 эпика c id=5 на DONE:");
//        subtask3.setStatus(TaskStatus.DONE);
//        manager.updateSubtask(subtask3);
//        System.out.println(epic2);
//        System.out.println(subtask3);
//        System.out.println();
//
//        System.out.println("Изменен статус подзадачи с id=2 эпика c id=4 на IN_PROGRESS:");
//        subtask2.setStatus(TaskStatus.IN_PROGRESS);
//        manager.updateSubtask(subtask2);
//        System.out.println(epic1);
//        System.out.println(subtask2);
//        System.out.println();
//
//        System.out.println("Изменен статус подзадач с id=1, id=2 эпика c id=4 на DONE:");
//        subtask1.setStatus(TaskStatus.DONE);
//        subtask2.setStatus(TaskStatus.DONE);
//        manager.updateSubtask(subtask1);
//        manager.updateSubtask(subtask2);
//        System.out.println(epic1);
//        System.out.println();
//
//        System.out.println("Удалена подзадача c id=2 эпика с id=4:");
//        manager.deleteSubtaskById(2);
//        System.out.println(epic1);
//        System.out.println();
//
//        System.out.println("Удален эпик с id=4, печатаем его и его подзадачу с id=1:");
//        manager.deleteEpicById(4);
//        epic2 = manager.getEpicById(4);
//        System.out.println(epic2);
//        subtask3 = manager.getSubtaskById(1);
//        System.out.println(subtask3);
//        System.out.println();
//
        Task task1 = new Task("Таск 1", "Описание таска 1");
        manager.addNewTask(task1);
        System.out.println(task1);
        manager.getTaskById(6);
//        manager.deleteTaskById(6);
//        System.out.println(manager.getTaskById(6));
//        manager.addNewTask(task1);
//        System.out.println(task1);

        System.out.println();
        System.out.println();
        List<Task> tasks = manager.getHistory();
        for (Task task : tasks) {
            System.out.print(task.getId());
        }
        manager.getEpicById(4);
        System.out.println();
        tasks = manager.getHistory();
        for (Task task : tasks) {
            System.out.print(task.getId());
        }

        manager.getSubtaskById(2);
        System.out.println();
        tasks = manager.getHistory();
        for (Task task : tasks) {
            System.out.print(task.getId());
        }

        manager.getEpicById(4);
        System.out.println();
        tasks = manager.getHistory();
        for (Task task : tasks) {
            System.out.print(task.getId());
        }

        manager.getSubtaskById(2);
        System.out.println();
        tasks = manager.getHistory();
        for (Task task : tasks) {
            System.out.print(task.getId());
        }

        manager.getEpicById(5);
        System.out.println();
        tasks = manager.getHistory();
        for (Task task : tasks) {
            System.out.print(task.getId());
        }

        manager.getSubtaskById(1);
        System.out.println();
        tasks = manager.getHistory();
        for (Task task : tasks) {
            System.out.print(task.getId());
        }

        manager.getTaskById(6);
        System.out.println();
        tasks = manager.getHistory();
        for (Task task : tasks) {
            System.out.print(task.getId());
        }

        manager.getSubtaskById(3);
        System.out.println();
        tasks = manager.getHistory();
        for (Task task : tasks) {
            System.out.print(task.getId());
        }

        manager.getSubtaskById(1);
        System.out.println();
        tasks = manager.getHistory();
        for (Task task : tasks) {
            System.out.print(task.getId());
        }

        manager.getSubtaskById(2);
        System.out.println("\nДальше будет происходить перезапись старых значений:");
        tasks = manager.getHistory();
        for (Task task : tasks) {
            System.out.print(task.getId());
        }

        manager.getSubtaskById(3);

        System.out.println();
        tasks = manager.getHistory();
        for (Task task : tasks) {
            System.out.print(task.getId());
        }
    }
}
