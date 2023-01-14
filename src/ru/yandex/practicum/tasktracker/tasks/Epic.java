package ru.yandex.practicum.tasktracker.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    protected LocalDateTime endTime;
    private List<Integer> subtasksId;

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        subtasksId = new ArrayList<>();
    }

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subtasksId = new ArrayList<>();
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void addSubtask(Integer subtaskId) {
        subtasksId.add(subtaskId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksId, epic.subtasksId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksId);
    }

    @Override
    public String toString() {
        String result = "" + id +
                "," + TaskType.EPIC +
                "," + name +
                "," + status +
                "," + description +
                ",,";

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
