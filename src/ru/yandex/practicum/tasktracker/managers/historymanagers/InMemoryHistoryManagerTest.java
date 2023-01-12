package ru.yandex.practicum.tasktracker.managers.historymanagers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.managers.Managers;
import ru.yandex.practicum.tasktracker.tasks.Task;
import ru.yandex.practicum.tasktracker.tasks.TaskStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    public static HistoryManager historyManager;
    public static Task task1;
    public static Task task2;
    public static Task task3;
    public static Task task4;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task(1, "Task1", "Task1 description", TaskStatus.NEW);
        task2 = new Task(2, "Task2", "Task2 description", TaskStatus.NEW);
        task3 = new Task(3, "Task3", "Task3 description", TaskStatus.NEW);
        task4 = new Task(4, "Task4", "Task4 description", TaskStatus.NEW);
    }

    @Test
    void shouldReturnEmptyHistoryList() {
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(0, history.size(), "Список не пуст.");
    }

    @Test
    void shouldReturnUniqueTasksList() {
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(1, history.size(), "Неверное количество задач.");
        assertEquals(task1, history.get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldReturnSecondAndThirdTasksWithoutFirst() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId());

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(2, history.size(), "Неверное количество задач.");

        List<Task> expectedTasks = List.of(task3, task2);

        assertEquals(expectedTasks, history, "Задачи не совпадают.");
    }

    @Test
    void shouldReturnFirstAndThirdTasksWithoutSecond() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(2, history.size(), "Неверное количество задач.");

        List<Task> expectedTasks = List.of(task3, task1);
        assertEquals(expectedTasks, history, "Задачи не совпадают.");
    }

    @Test
    void shouldReturnFirstAndSecondTasksWithoutThird() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId());

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(2, history.size(), "Неверное количество задач.");

        List<Task> expectedTasks = List.of(task2, task1);
        assertEquals(expectedTasks, history, "Задачи не совпадают.");
    }

    @Test
    void add() {
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не возвращается.");
        assertEquals(1, history.size(), "Неверное количество задач в истории.");
        assertEquals(task1, history.get(0), "Задачи не совпадают.");
    }

    @Test
    void remove() {
        historyManager.add(task1);
        historyManager.remove(task1.getId());
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не возвращается.");
        assertEquals(0, history.size(), "Неверное количество задач в истории.");
    }

    @Test
    void getHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);

        List<Task> expectedHistory = List.of(task4, task3, task2, task1);
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не возвращается.");
        assertEquals(expectedHistory.size(), history.size(), "Неверное количество задач в истории.");
        assertEquals(expectedHistory, history, "Задачи не совпадают.");
    }
}