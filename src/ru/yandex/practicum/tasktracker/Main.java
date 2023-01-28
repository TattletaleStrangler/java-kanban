package ru.yandex.practicum.tasktracker;

import ru.yandex.practicum.tasktracker.managers.taskmanagers.HttpTaskManager;
import ru.yandex.practicum.tasktracker.managers.taskmanagers.TaskManager;
import ru.yandex.practicum.tasktracker.tasks.Epic;
import ru.yandex.practicum.tasktracker.tasks.Subtask;
import ru.yandex.practicum.tasktracker.tasks.Task;
import ru.yandex.practicum.tasktracker.tasks.TaskStatus;
import ru.yandex.practicum.tasktracker.web.server.KVServer;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        KVServer server = new KVServer();
        server.start();
        TaskManager manager = new HttpTaskManager("http://localhost:8078");
//        TaskManager manager = FileBackedTasksManager.loadFromFile(new File(System.getProperty("user.home") + "\\backup.csv"));
//        TaskManager manager = new InMemoryTaskManager();
//        TaskManager manager = new FileBackedTasksManager(new File(System.getProperty("user.home") + "\\backup.csv"), false);
        Task task2 = new Task("Таск 2", "Описание таска 2");
        task2.setStartTime(LocalDateTime.now());
        task2.setDuration(Duration.ofMinutes(45));
        manager.addNewTask(task2);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        manager.addNewEpic(epic2);

        Subtask subtask2 = new Subtask("Подзадача 1 Эпика 2", "Описание подзадачи 1 эпика 2", epic2.getId());
        subtask2.setStartTime(LocalDateTime.now().plusHours(1));
        subtask2.setDuration(Duration.ofHours(4));

        Subtask subtask3 = new Subtask("Подзадача 2 Эпика 2", "Описание подзадачи 2 эпика 2", epic2.getId(), TaskStatus.IN_PROGRESS);
        subtask3.setStartTime(LocalDateTime.now().plusHours(8));
        subtask3.setDuration(Duration.ofHours(5));

        manager.addNewSubtask(subtask2);
        manager.addNewSubtask(subtask3);

        List<Task> priorityTasks = manager.getPrioritizedTasks();

        manager.getTaskById(1);
        manager.getSubtaskById(3);
        Epic epic3 = manager.getEpicById(2);
        manager.getSubtaskById(4);

        //повторяющиеся:

        manager.getTaskById(1);
        manager.getEpicById(2);

        manager.deleteTaskById(1);
        manager.deleteEpicById(2);
        manager.deleteSubtaskById(3);

    }
}
