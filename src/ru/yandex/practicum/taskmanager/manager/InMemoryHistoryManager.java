package ru.yandex.practicum.taskmanager.manager;

import ru.yandex.practicum.taskmanager.tasks.Epic;
import ru.yandex.practicum.taskmanager.tasks.SubTask;
import ru.yandex.practicum.taskmanager.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int HISTORY_CAPACITY = 10;

    private final List<Task> history = new ArrayList<>(HISTORY_CAPACITY);

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        Task taskCopy;

        if (task instanceof SubTask) {
            taskCopy = new SubTask(task.getTitle(), task.getDescription(), task.getId(), task.getStatus(), ((SubTask) task).getEpicId());
        } else if (task instanceof Epic) {
            taskCopy = new Epic(task.getTitle(), task.getDescription(), task.getId(), task.getStatus());
            ArrayList<Integer> subtasks = ((Epic) task).getSubtasksIds();
            for (Integer subtask : subtasks) {
                ((Epic) taskCopy).addSubtaskId(subtask);
            }
        } else {
            taskCopy = new Task(task.getTitle(), task.getDescription(), task.getId(), task.getStatus());
        }

        if (history.size() == HISTORY_CAPACITY) {
            history.remove(0);
        }
        history.add(taskCopy);
    }
}