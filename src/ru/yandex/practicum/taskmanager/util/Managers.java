package ru.yandex.practicum.taskmanager.util;

import ru.yandex.practicum.taskmanager.manager.HistoryManager;
import ru.yandex.practicum.taskmanager.manager.InMemoryHistoryManager;
import ru.yandex.practicum.taskmanager.manager.InMemoryTaskManager;
import ru.yandex.practicum.taskmanager.manager.TaskManager;

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
}