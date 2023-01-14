package ru.yandex.practicum.tasktracker.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.tasks.Task;
import ru.yandex.practicum.tasktracker.tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    public int taskId;
    public String name;
    public String description;
    public TaskStatus status;

    public Task task;

    @BeforeEach
    public void beforeEach() {
        taskId = 1;
        name = "Task";
        description = "Task description";
        status = TaskStatus.NEW;
        task = new Task(taskId, name, description, status);
    }

    @Test
    void getId() {
        int receivedId = task.getId();
        assertEquals(taskId, receivedId, "Id не совпадают.");
    }

    @Test
    void setId() {
        int idToSet = 2;
        task.setId(idToSet);

        int setId = task.getId();
        assertEquals(idToSet, setId, "Id не совпадают.");
    }

    @Test
    void getName() {
        String receivedId = task.getName();
        assertEquals(name, receivedId, "Имена задачи не совпадают.");
    }

    @Test
    void setName() {
        String nameToSet = "New Task Name";
        task.setName(nameToSet);

        String setName = task.getName();
        assertEquals(nameToSet, setName, "Имена задачи не совпадают.");
    }

    @Test
    void getDescription() {
        String receivedDescription = task.getDescription();
        assertEquals(description, receivedDescription, "Описания задачи не совпадают.");
    }

    @Test
    void setDescription() {
        String descriptionToSet = "New description";
        task.setDescription(descriptionToSet);

        String setDescription = task.getDescription();
        assertEquals(descriptionToSet, setDescription, "Описания задачи не совпадают.");
    }

    @Test
    void getStatus() {
        TaskStatus receivedStatus = task.getStatus();
        assertEquals(status, receivedStatus, "Статусы задачи не совпадают.");
    }

    @Test
    void setStatus() {
        TaskStatus statusToSet = TaskStatus.IN_PROGRESS;
        task.setStatus(statusToSet);

        TaskStatus setStatus = task.getStatus();
        assertEquals(statusToSet, setStatus, "Статусы задачи не совпадают.");
    }

    @Test
    void testEquals() {
        Task task1 = new Task(taskId, name, description, TaskStatus.IN_PROGRESS);
        Task task2 = new Task(taskId, name, description, TaskStatus.IN_PROGRESS);

        assertEquals(task1, task2, "Задачи не равны.");
        assertEquals(task2, task1, "Задачи не равны.");
        assertEquals(task1, task1, "Задачи не равны.");
        assertFalse(task1.equals(null), "Задача равна null.");
        for (int i = 0; i < 10; i++) {
            assertEquals(task1, task2, "Задачи не равны.");
        }
    }

    @Test
    void testHashCode() {
        final int expectedHashCode = Objects.hash(taskId, name, description);
        assertEquals(expectedHashCode, task.hashCode());
    }

    @Test
    void testToString() {
        LocalDateTime startTime = LocalDateTime.now();
        task.setStartTime(startTime);
        Duration duration = Duration.ofHours(9);
        task.setDuration(duration);

        final String expected = "1,TASK,Task,NEW,Task description,," + duration
                + "," + startTime.format(Task.DATE_TIME_FORMATTER);
        assertEquals(expected, task.toString(), "Метод toString() работает неверно.");
    }

    @Test
    void getEndTime() {
        LocalDateTime startTime = LocalDateTime.now();
        task.setStartTime(startTime);
        Duration duration = Duration.ofDays(7);
        task.setDuration(duration);

        LocalDateTime expectedEndTime = startTime.plus(duration);
        LocalDateTime endTime = task.getEndTime();
        assertNotNull(endTime, "Время завершения задачи не возвращается.");
        assertEquals(expectedEndTime, endTime, "Время завершения задачи отличается от заданного.");
    }

    @Test
    void getDuration() {
        Duration duration = Duration.ofHours(24);
        task.setDuration(duration);

        Duration setDuration = task.getDuration();
        assertNotNull(setDuration, "Продолжительность задачи не возвращается.");
        assertEquals(duration, setDuration, "Продолжительность задачи отличается от заданной.");
    }

    @Test
    void setDuration() {
        Duration duration = Duration.ofMinutes(11);
        task.setDuration(duration);

        Duration setDuration = task.getDuration();
        assertNotNull(setDuration, "Продолжительность задачи не возвращается.");
        assertEquals(duration, setDuration, "Продолжительность задачи отличается от заданной.");
    }

    @Test
    void getStartTime() {
        LocalDateTime startTime = LocalDateTime.now();
        task.setStartTime(startTime);

        LocalDateTime setTime = task.getStartTime();
        assertNotNull(setTime, "Стартовое время не возвращается.");
        assertEquals(startTime, setTime, "Стартовое время отличается от заданного.");
    }

    @Test
    void setStartTime() {
        LocalDateTime startTime = LocalDateTime.now();
        task.setStartTime(startTime);

        LocalDateTime setTime = task.getStartTime();
        assertNotNull(setTime, "Стартовое время не возвращается.");
        assertEquals(startTime, setTime, "Стартовое время отличается от заданного.");
    }
}