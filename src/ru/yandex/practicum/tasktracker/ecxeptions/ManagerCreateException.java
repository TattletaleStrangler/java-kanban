package ru.yandex.practicum.tasktracker.ecxeptions;

public class ManagerCreateException extends RuntimeException{
    public ManagerCreateException(String message) {
        super(message);
    }
}