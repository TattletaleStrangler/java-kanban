package ru.yandex.practicum.tasktracker;

import ru.yandex.practicum.tasktracker.manager.Manager;
import ru.yandex.practicum.tasktracker.tasks.*;

public class Main {

    public static void main(String[] args) {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Subtask subtask1 = new Subtask("Подзадача 1 Эпика 1", "Описание подзадачи 1 эпика 1");
        Subtask subtask2 = new Subtask("Подзадача 2 Эпика 1", "Описание подзадачи 2 эпика 1");


        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        Subtask subtask3 = new Subtask("Подзадача 1 Эпика 2", "Описание подзадачи 1 эпика 2");


        Manager manager = new Manager();
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

        System.out.println("Изменен статус подзадачи с id=3 эпика c id=5 на DONE:");
        subtask3.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask3);
        System.out.println(epic2);
        System.out.println(subtask3);
        System.out.println();

        System.out.println("Изменен статус подзадачи с id=2 эпика c id=4 на IN_PROGRESS:");
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask2);
        System.out.println(epic1);
        System.out.println(subtask2);
        System.out.println();

        System.out.println("Изменен статус подзадач с id=1, id=2 эпика c id=4 на DONE:");
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        System.out.println(epic1);
        System.out.println();

        System.out.println("Удалена подзадача c id=2 эпика с id=4:");
        manager.deleteSubtaskById(2);
        System.out.println(epic1);
        System.out.println();

        System.out.println("Удален эпик с id=4, печатаем его и его подзадачу с id=1:");
        manager.deleteEpicById(4);
        epic2 = manager.getEpicById(4);
        System.out.println(epic2);
        subtask3 = manager.getSubtaskById(1);
        System.out.println(subtask3);
        System.out.println();

        Task task1 = new Task("Таск 1", "Описание таска 1");
        manager.addNewTask(task1);
        System.out.println(task1);
        manager.getTaskById(6);
        manager.deleteTaskById(6);
        System.out.println(manager.getTaskById(6));
        manager.addNewTask(task1);
        System.out.println(task1);
    }
}
