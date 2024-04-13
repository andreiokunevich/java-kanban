package ru.yandex.practicum.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.tasks.Status;
import ru.yandex.practicum.taskmanager.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

abstract class HistoryManagerTest {
    protected HistoryManager historyManager;

    protected abstract HistoryManager createHistoryManager();

    @BeforeEach
    void create() {
        historyManager = createHistoryManager();
    }

    @Test
    void checkEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldNotBeAbleAddDuplicates() {
        Task task1 = new Task("Task_1", "Task_1", 1, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);

        assertEquals(1, historyManager.getHistory().size(), "Произошло добавление дубликата в историю.");
    }

    @Test
    void deleteTaskFromBeginningOfHistory() {
        Task task1 = new Task("Task_1", "Task_1", 1, Status.NEW,
                LocalDateTime.of(2024, 4, 1, 23, 0), Duration.ofMinutes(50));
        Task task2 = new Task("Task_1", "Task_1", 2, Status.NEW,
                LocalDateTime.of(2024, 4, 2, 23, 0), Duration.ofMinutes(50));
        Task task3 = new Task("Task_1", "Task_1", 3, Status.NEW,
                LocalDateTime.of(2024, 4, 3, 23, 0), Duration.ofMinutes(50));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId());

        assertEquals(2, historyManager.getHistory().size(), "Задача не удалилась из стории.");
        assertFalse(historyManager.getHistory().contains(task1), "Задача не удалилась из стории.");
    }

    @Test
    void deleteTaskFromMiddleOfHistory() {
        Task task1 = new Task("Task_1", "Task_1", 1, Status.NEW,
                LocalDateTime.of(2024, 4, 1, 23, 0), Duration.ofMinutes(50));
        Task task2 = new Task("Task_1", "Task_1", 2, Status.NEW,
                LocalDateTime.of(2024, 4, 2, 23, 0), Duration.ofMinutes(50));
        Task task3 = new Task("Task_1", "Task_1", 3, Status.NEW,
                LocalDateTime.of(2024, 4, 3, 23, 0), Duration.ofMinutes(50));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        System.out.println(historyManager.getHistory().size());
        historyManager.remove(task2.getId());

        assertEquals(2, historyManager.getHistory().size(), "Задача не удалилась из стории.");
        assertFalse(historyManager.getHistory().contains(task2), "Задача не удалилась из стории.");
    }

    @Test
    void deleteTaskFromEndOfHistory() {
        Task task1 = new Task("Task_1", "Task_1", 1, Status.NEW,
                LocalDateTime.of(2024, 4, 1, 23, 0), Duration.ofMinutes(50));
        Task task2 = new Task("Task_1", "Task_1", 2, Status.NEW,
                LocalDateTime.of(2024, 4, 2, 23, 0), Duration.ofMinutes(50));
        Task task3 = new Task("Task_1", "Task_1", 3, Status.NEW,
                LocalDateTime.of(2024, 4, 3, 23, 0), Duration.ofMinutes(50));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId());

        assertEquals(2, historyManager.getHistory().size(), "Задача не удалилась из стории.");
        assertFalse(historyManager.getHistory().contains(task3), "Задача не удалилась из стории.");
    }
}