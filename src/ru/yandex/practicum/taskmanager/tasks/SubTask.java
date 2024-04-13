package ru.yandex.practicum.taskmanager.tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    protected int epicId;

    public SubTask(String title, String description, int id, Status status, int epicId, LocalDateTime startTime, Duration duration) {
        super(title, description, id, status, startTime, duration);
        this.epicId = epicId;
        this.type = Type.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + getDuration().toMinutes() +
                ", startTime=" + getStartTime().format(formatter) +
                ", endTime=" + getEndTime().format(formatter) +
                '}';
    }
}