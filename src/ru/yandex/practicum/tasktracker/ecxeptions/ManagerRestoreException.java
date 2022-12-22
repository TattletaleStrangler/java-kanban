package ru.yandex.practicum.tasktracker.ecxeptions;

public class ManagerRestoreException extends RuntimeException{
    public ManagerRestoreException(String message) {
        super(message);
    }
}
