package ru.yandex.practicum.tasktracker.ecxeptions;

public class TaskTimeException extends RuntimeException{
    public TaskTimeException(String message) {
        super(message);
    }
}
