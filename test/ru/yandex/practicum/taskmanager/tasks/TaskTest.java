package ru.yandex.practicum.taskmanager.tasks;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.manager.TaskManager;
import ru.yandex.practicum.taskmanager.util.Managers;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    TaskManager inMemoryTaskManager = Managers.getInMemoryTaskManager();

    @Test
    void checkEqualityOfTasks() {
        Task task1 = new Task("Task1", "Task1", 0, Status.NEW);
        Task task2 = new Task("Task1", "Task1", 0, Status.NEW);
        int task1Id = inMemoryTaskManager.createTask(task1);
        int task2Id = inMemoryTaskManager.createTask(task2);

        assertNotEquals(task1, task2, "Задачи с разными ID, но одинаковым содержимым оказались равны.");
    }
}