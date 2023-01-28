package ru.yandex.practicum.tasktracker.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(int id, String name, String description, int epicId, TaskStatus status, Duration duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, int epicId, TaskStatus status) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId) {
        super(name, description, TaskStatus.NEW);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId, TaskStatus status) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(epicId, subtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        String result = "" + id +
                "," + TaskType.SUBTASK +
                "," + name +
                "," + status +
                "," + description +
                "," + epicId +
                ",";

        if (duration != null && !duration.equals(Duration.ZERO)) {
            result += duration + ",";
        } else {
            result += ",";
        }

        if (startTime != null) {
            result += startTime.format(DATE_TIME_FORMATTER);
        }

        return result;
    }
}
