package ru.yandex.practicum.taskmanager.tasks;

public class SubTask extends Task {

    protected int epicId;

    public SubTask(String title, String description, int id, Status status, int epicId) {
        super(title, description, id, status);
        this.epicId = epicId;
        this.type = Type.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return id + ","
                + type + ","
                + title + ","
                + status + ","
                + description + ","
                + epicId;
    }
}