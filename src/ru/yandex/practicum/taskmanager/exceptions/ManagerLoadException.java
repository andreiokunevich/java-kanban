package ru.yandex.practicum.taskmanager.exceptions;

public class ManagerLoadException extends RuntimeException {
    public ManagerLoadException(String message) {
        super(message);
    }
}