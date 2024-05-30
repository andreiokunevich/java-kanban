package ru.yandex.practicum.taskmanager.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {

    protected String title;
    protected String description;
    protected int id;
    protected Status status;
    protected Type type;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected final transient LocalDateTime defaultDateTime = LocalDateTime.MAX;
    protected final transient DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public Task(String title, String description, int id, Status status, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
        this.type = Type.TASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return startTime == null ? defaultDateTime : startTime.plus(duration);
    }

    public Type getType() {
        return type;
    }

    public Duration getDuration() {
        return getEndTime() == defaultDateTime ? Duration.ZERO : duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime == null ? defaultDateTime : startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + getDuration().toMinutes() +
                ", startTime=" + getStartTime().format(formatter) +
                ", endTime=" + getEndTime().format(formatter) +
                '}';
    }
}