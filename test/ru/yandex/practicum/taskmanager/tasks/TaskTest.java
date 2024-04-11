package ru.yandex.practicum.taskmanager.tasks;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.manager.TaskManager;
import ru.yandex.practicum.taskmanager.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    TaskManager inMemoryTaskManager = Managers.getInMemoryTaskManager();

    @Test
    void checkEqualityOfTasks() {
        Task task1 = new Task("Task_1", "Task_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        Task task2 = new Task("Task_1", "Task_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        int task1Id = inMemoryTaskManager.createTask(task1);
        int task2Id = inMemoryTaskManager.createTask(task2);

        assertNotEquals(task1, task2, "Задачи с разными ID, но одинаковым содержимым оказались равны.");
    }
}