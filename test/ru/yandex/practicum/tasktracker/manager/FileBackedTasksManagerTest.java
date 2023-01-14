package ru.yandex.practicum.tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.managers.taskmanagers.FileBackedTasksManager;
import ru.yandex.practicum.tasktracker.tasks.Epic;
import ru.yandex.practicum.tasktracker.tasks.Subtask;
import ru.yandex.practicum.tasktracker.tasks.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.tasktracker.tasks.TaskStatus.NEW;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private static final File backup = new File(System.getProperty("user.home") + "\\backup.csv");

    @BeforeEach
    void beforeEach() {
        taskManager = new FileBackedTasksManager(backup, false);
    }


    @Test
    void shouldReturnEmptyTasksList() {
        List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(0, tasks.size(), "Список задач не пуст.");

        List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(0, epics.size(), "Список эпиков не пуст.");

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(0, subtasks.size(), "Список подзадач не пуст.");
    }

    @Test
    void shouldReturnEmptyHistory() {
        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(0, history.size(), "История не пуста.");
    }

    @Test
    void shouldSaveAndReturnEpicWithoutSubtasks() {
        Epic epic = new Epic("Test epic", "Test epic description");
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(1, history.size(), "История не пуста.");
        assertEquals(savedEpic, history.get(0));
    }

    @Test
    void shouldRestoreTasksAndHistory() {
        Task task = new Task("Test getHistory task", "Test getHistory task description", NEW);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(40));
        final int taskId = taskManager.addNewTask(task);

        Epic epic = new Epic("Test getHistory epic", "Test getHistory epic description");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test getHistory subtask 1", "Test getHistory subtask 1 description"
                , epicId, NEW);
        subtask1.setStartTime(LocalDateTime.now().plusHours(1));
        subtask1.setDuration(Duration.ofMinutes(40));
        final int subtask1Id = taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test getHistory subtask 2", "Test getHistory subtask 2 description"
                , epicId, NEW);
        subtask2.setStartTime(LocalDateTime.now().plusHours(2));
        subtask2.setDuration(Duration.ofMinutes(40));
        final int subtask2Id = taskManager.addNewSubtask(subtask2);

        Subtask subtask3 = new Subtask("Test getHistory subtask 3", "Test getHistory subtask 3 description"
                , epicId, NEW);
        subtask3.setStartTime(LocalDateTime.now().plusHours(3));
        subtask3.setDuration(Duration.ofMinutes(50));
        final int subtask3Id = taskManager.addNewSubtask(subtask3);

        taskManager = FileBackedTasksManager.loadFromFile(backup);
        assertNotNull(taskManager, "Менеджер не создан.");

        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        List<Subtask> expectedSubtasks = List.of(subtask3, subtask2, subtask1);
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Список подзадач пуст.");
        assertEquals(expectedSubtasks.size(), subtasks.size(), "Неверное число подзадач в списке.");
        for (Subtask subtask : expectedSubtasks) {
            assertTrue(subtasks.contains(subtask), "Списки подзадач не совпадают.");
        }

        final Task savedTask = taskManager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final Subtask savedSubtask = taskManager.getSubtaskById(subtask1Id);
        assertNotNull(savedSubtask, "Подзадача не найдена.");

        List<Task> expectedHistory = List.of(savedSubtask, savedTask, savedEpic);
        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertIterableEquals(expectedHistory, history, "История не соответстует ожидаемой");

        List<Task> expectedPrioritizedTasks = List.of(task, subtask1, subtask2, subtask3);
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertNotNull(prioritizedTasks, "Список приоритетных задач не возвращается.");
        assertIterableEquals(expectedPrioritizedTasks, prioritizedTasks, "Списки приоритетных задач не совпадают.");
    }
}