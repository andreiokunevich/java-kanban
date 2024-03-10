package ru.yandex.practicum.taskmanager.tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskIds;

    public Epic(String title, String description, int id, Status status) {
        super(title, description, id, status);
        subtaskIds = new ArrayList<>();
    }

    public void addSubtaskId(Integer idSubtask) {
        subtaskIds.add(idSubtask);
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtaskIds;
    }

    public void cleanSubtaskIds() {
        subtaskIds.clear();
    }

    public void removeSubtask(Integer idSubtask) {
        subtaskIds.remove(idSubtask);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}' + '\n';
    }
}