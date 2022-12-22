package ru.yandex.practicum.tasktracker.ecxeptions;

public class TaskReadException extends Exception{
    public TaskReadException(String message) {
        super(message);
    }
}
