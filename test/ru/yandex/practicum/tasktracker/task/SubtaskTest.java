package ru.yandex.practicum.tasktracker.task;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.tasks.Subtask;
import ru.yandex.practicum.tasktracker.tasks.Task;
import ru.yandex.practicum.tasktracker.tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void getEpicId() {
        final int epicId = 1;
        Subtask subtask = new Subtask(2, "Subtask", "Subtask description", epicId, TaskStatus.DONE);
        assertNotNull(subtask, "Подзадача не создана");

        final int epicIdFromSubtask = subtask.getEpicId();
        assertEquals(epicId, epicIdFromSubtask, "Id эпиков не совпадают.");
    }

    @Test
    void setEpicId() {
        final int epicIdOld = 1;
        Subtask subtask = new Subtask(2, "Subtask", "Subtask description", epicIdOld, TaskStatus.DONE);
        assertNotNull(subtask, "Подзадача не создана");

        final int epicIdNew = 3;
        subtask.setEpicId(epicIdNew);

        final int epicIdFromSubtask = subtask.getEpicId();
        assertEquals(epicIdNew, epicIdFromSubtask, "Id эпиков не совпадают.");
    }

    @Test
    void testEquals() {
        final int epicId = 1;
        Subtask subtask1 = new Subtask(2, "Subtask", "Subtask description", epicId
                , TaskStatus.IN_PROGRESS);
        Subtask subtask2 = new Subtask(2, "Subtask", "Subtask description", epicId
                , TaskStatus.IN_PROGRESS);

        assertEquals(subtask1, subtask2, "Подзадачи не равны.");
        assertEquals(subtask2, subtask1, "Подзадачи не равны.");
        assertEquals(subtask1, subtask1, "Подзадачи не равны.");
        assertFalse(subtask1.equals(null), "Задача равна null.");
        for (int i = 0; i < 10; i++) {
            assertEquals(subtask1, subtask2, "Подзадачи не равны.");
        }
    }

    @Test
    void testHashCode() {
        final int epicId = 1;
        final int subtaskId = 2;
        final String name = "Subtask";
        final String description = "Subtask description";

        Subtask subtask = new Subtask(subtaskId, name, description, epicId
                , TaskStatus.IN_PROGRESS);

        final int expectedHashCode = Objects.hash(Objects.hash(subtaskId, name, description), epicId);
        assertEquals(expectedHashCode, subtask.hashCode());
    }

    @Test
    void testToString() {
        final int epicId = 1;
        Subtask subtask = new Subtask(2, "Subtask", "Subtask description", epicId
                , TaskStatus.IN_PROGRESS);
        LocalDateTime startTime = LocalDateTime.now();
        subtask.setStartTime(startTime);
        Duration duration = Duration.ofHours(13);
        subtask.setDuration(duration);

        final String expected = "2,SUBTASK,Subtask,IN_PROGRESS,Subtask description," + epicId + ","
                + duration + "," + startTime.format(Task.DATE_TIME_FORMATTER);
        assertEquals(expected, subtask.toString(), "Метод toString() работает неверно.");
    }
}