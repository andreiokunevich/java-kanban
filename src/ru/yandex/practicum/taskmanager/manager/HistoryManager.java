package ru.yandex.practicum.taskmanager.manager;

import ru.yandex.practicum.taskmanager.tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}