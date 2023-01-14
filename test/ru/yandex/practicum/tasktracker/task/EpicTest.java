package ru.yandex.practicum.tasktracker.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.managers.taskmanagers.InMemoryTaskManager;
import ru.yandex.practicum.tasktracker.managers.taskmanagers.TaskManager;
import ru.yandex.practicum.tasktracker.tasks.Epic;
import ru.yandex.practicum.tasktracker.tasks.Subtask;
import ru.yandex.practicum.tasktracker.tasks.Task;
import ru.yandex.practicum.tasktracker.tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    public static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldReturnStatusNewWithEmptySubtasksList() {
        Epic epic = new Epic("Test epic", "Description test epic");
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Задача не найдена.");
        Assertions.assertEquals(TaskStatus.NEW, savedEpic.getStatus(), "Статусы не совпадают.");
    }

    @Test
    void shouldReturnStatusNewWithThreeNewSubtasks() {
        Epic epic = new Epic("Test epic", "Description test epic");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test epic subtask 1", "Description of test epic subtask 1", epicId, TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Test epic subtask 2", "Description of test epic subtask 2", epicId, TaskStatus.NEW);
        Subtask subtask3 = new Subtask("Test epic subtask 3", "Description of test epic subtask 3", epicId, TaskStatus.NEW);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(TaskStatus.NEW, savedEpic.getStatus(), "Статусы не совпадают.");
    }

    @Test
    void shouldReturnStatusDoneWithThreeDoneSubtasks() {
        Epic epic = new Epic("Test epic", "Description test epic");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test epic subtask 1", "Description of test epic subtask 1", epicId, TaskStatus.DONE);
        Subtask subtask2 = new Subtask("Test epic subtask 2", "Description of test epic subtask 2", epicId, TaskStatus.DONE);
        Subtask subtask3 = new Subtask("Test epic subtask 3", "Description of test epic subtask 3", epicId, TaskStatus.DONE);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(TaskStatus.DONE, savedEpic.getStatus(), "Статусы не совпадают.");
    }

    @Test
    void shouldReturnStatusInProgressWithOneDoneSubtaskAndOneNewSubtask() {
        Epic epic = new Epic("Test epic", "Description test epic");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test epic subtask 1", "Description of test epic subtask 1", epicId, TaskStatus.DONE);
        Subtask subtask2 = new Subtask("Test epic subtask 2", "Description of test epic subtask 2", epicId, TaskStatus.NEW);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus(), "Статусы не совпадают.");
    }

    @Test
    void shouldReturnStatusInProgressWithThreeInProgressSubtasks() {
        Epic epic = new Epic("Test epic", "Description test epic");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test epic subtask 1", "Description of test epic subtask 1", epicId, TaskStatus.IN_PROGRESS);
        Subtask subtask2 = new Subtask("Test epic subtask 2", "Description of test epic subtask 2", epicId, TaskStatus.IN_PROGRESS);
        Subtask subtask3 = new Subtask("Test epic subtask 3", "Description of test epic subtask 3", epicId, TaskStatus.IN_PROGRESS);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus(), "Статусы не совпадают.");
    }

    @Test
    void getSubtasksId() {
        Epic epic = new Epic("Test epic", "Description test epic");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test epic subtask 1", "Description of test epic subtask 1", epicId, TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Test epic subtask 2", "Description of test epic subtask 2", epicId, TaskStatus.NEW);
        Subtask subtask3 = new Subtask("Test epic subtask 3", "Description of test epic subtask 3", epicId, TaskStatus.NEW);
        List<Integer> expectedSubtasksId = new ArrayList<>();
        expectedSubtasksId.add(taskManager.addNewSubtask(subtask1));
        expectedSubtasksId.add(taskManager.addNewSubtask(subtask2));
        expectedSubtasksId.add(taskManager.addNewSubtask(subtask3));

        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Задача не найдена.");

        final List<Integer> savedSubtasksId = savedEpic.getSubtasksId();
        assertNotNull(savedSubtasksId, "Задачи не возвращаются.");
        assertEquals(3, savedSubtasksId.size(), "Неверное количество задач.");
        assertIterableEquals(expectedSubtasksId, savedSubtasksId, "Задачи не совпадают.");
    }

    @Test
    void addSubtask() {
        Epic epic = new Epic(1, "Epic", "Epic description", TaskStatus.DONE);
        Subtask subtask = new Subtask(2, "Subtask", "Subtask description", 1, TaskStatus.DONE);
        epic.addSubtask(subtask.getId());

        assertEquals(subtask.getId(), epic.getSubtasksId().get(0), "Задачи не совпадают.");
    }

    @Test
    void testEquals() {
        Epic epic1 = new Epic(1, "Epic", "Epic description", TaskStatus.DONE);
        Epic epic2 = new Epic(1, "Epic", "Epic description", TaskStatus.DONE);
        Subtask subtask1 = new Subtask(2, "Subtask", "Subtask description", 1, TaskStatus.DONE);
        Subtask subtask2 = new Subtask(3, "Subtask", "Subtask description", 1, TaskStatus.DONE);

        epic1.addSubtask(subtask1.getId());
        epic1.addSubtask(subtask2.getId());

        epic2.addSubtask(subtask1.getId());
        epic2.addSubtask(subtask2.getId());

        assertEquals(epic1, epic2, "Задачи не равны.");
        assertEquals(epic2, epic1, "Задачи не равны.");
        assertEquals(epic1, epic1, "Задачи не равны.");
        assertFalse(epic1.equals(null), "Задача равна null.");
        for (int i = 0; i < 10; i++) {
            assertEquals(epic1, epic2, "Задачи не равны.");
        }
    }

    @Test
    void testHashCode() {
        final int id = 1;
        final String name = "Epic";
        final String description = "Epic description";

        final Epic epic = new Epic(id, name, description, TaskStatus.DONE);
        final Subtask subtask1 = new Subtask(2, "Subtask", "Subtask description", 1, TaskStatus.DONE);
        final Subtask subtask2 = new Subtask(3, "Subtask", "Subtask description", 1, TaskStatus.DONE);
        epic.addSubtask(subtask1.getId());
        epic.addSubtask(subtask2.getId());

        final List<Integer> subtasksId = new ArrayList<>();
        subtasksId.add(subtask1.getId());
        subtasksId.add(subtask2.getId());

        final int expectedHash = Objects.hash(Objects.hash(id, name, description), subtasksId);
        assertEquals(expectedHash, epic.hashCode(), "Метод hashCode() работает неверно");
    }

    @Test
    void testToString() {
        Epic epic = new Epic("Epic", "Epic description");
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(TaskStatus.NEW, savedEpic.getStatus(), "Статусы не совпадают.");

        Subtask subtask1 = new Subtask(2, "Subtask", "Subtask description", epicId, TaskStatus.DONE);
        LocalDateTime subtaskStartTime1 = LocalDateTime.now();
        subtask1.setStartTime(subtaskStartTime1);
        Duration subtaskDuration1 = Duration.ofHours(13);
        subtask1.setDuration(subtaskDuration1);

        Subtask subtask2 = new Subtask(3, "Subtask", "Subtask description", epicId, TaskStatus.DONE);
        LocalDateTime subtaskStartTime2 = subtaskStartTime1.plusDays(2);
        subtask2.setStartTime(subtaskStartTime2);
        Duration subtaskDuration2 = Duration.ofMinutes(156);
        subtask2.setDuration(subtaskDuration2);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        Epic receivedEpic = taskManager.getEpicById(epicId);
        Duration epicDuration = subtaskDuration1.plus(subtaskDuration2);
        String epicStartTime = subtaskStartTime1.format(Task.DATE_TIME_FORMATTER);
        String epicEndTime = subtask2.getEndTime().format(Task.DATE_TIME_FORMATTER);

        final String expected = "1,EPIC,Epic,DONE,Epic description,," + epicDuration + "," + epicStartTime;
        assertEquals(expected, receivedEpic.toString(), "Метод toString() работает неверно.");
    }
}