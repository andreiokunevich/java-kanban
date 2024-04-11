package ru.yandex.practicum.taskmanager.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskIds;
    protected LocalDateTime endTime;

    public Epic(String title, String description, int id, Status status, LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        super(title, description, id, status, startTime, duration);
        this.type = Type.EPIC;
        subtaskIds = new ArrayList<>();
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
                ", duration=" + getDuration().toMinutes() +
                ", startTime=" + getStartTime().format(formatter) +
                ", endTime=" + getEndTime().format(formatter) +
                '}';
    }
}