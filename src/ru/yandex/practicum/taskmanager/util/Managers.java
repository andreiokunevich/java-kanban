package ru.yandex.practicum.taskmanager.util;

import ru.yandex.practicum.taskmanager.manager.*;

import java.io.File;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return getInMemoryTaskManager();
    }

    public static InMemoryTaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return getInMemoryHistoryManager();
    }

    public static InMemoryHistoryManager getInMemoryHistoryManager() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(file);
    }
}