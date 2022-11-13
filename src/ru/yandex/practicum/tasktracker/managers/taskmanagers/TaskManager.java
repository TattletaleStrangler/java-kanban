package ru.yandex.practicum.tasktracker.managers.taskmanagers;

import ru.yandex.practicum.tasktracker.tasks.Epic;
import ru.yandex.practicum.tasktracker.tasks.Subtask;
import ru.yandex.practicum.tasktracker.tasks.Task;

import java.util.List;

public interface TaskManager {

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Task> getTasks();

    void deleteEpics();

    void deleteSubtasks();

    void deleteTasks();

    Epic getEpicById(Integer id);

    Subtask getSubtaskById(Integer id);

    Task getTaskById(Integer id);

    Integer addNewTask(Task task);

    Integer addNewEpic(Epic epic);

    Integer addNewSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void deleteEpicById(Integer id);

    void deleteSubtaskById(Integer id);

    void deleteTaskById(Integer id);

    List<Subtask> getSubtasksByEpic(Epic epic);

    List<Task> getHistory();
}
