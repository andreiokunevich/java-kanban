package ru.yandex.practicum.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.tasks.Epic;
import ru.yandex.practicum.taskmanager.tasks.Status;
import ru.yandex.practicum.taskmanager.tasks.SubTask;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest extends HistoryManagerTest {

    private TaskManager inMemoryTaskManager;

    @Override
    protected HistoryManager createHistoryManager() {
        return Managers.getDefaultHistory();
    }

    @BeforeEach
    void createNewTaskManager() {
        inMemoryTaskManager = Managers.getInMemoryTaskManager();
    }

    @Test
    void getHistory() {
        Task task = new Task("Task_1", "Task_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        int taskId = inMemoryTaskManager.createTask(task);
        Task savedTaskNew = inMemoryTaskManager.getTaskById(taskId);

        final List<Task> history = inMemoryTaskManager.getHistory();

        assertNotNull(history, "Не удалось получить историю.");
    }

    @Test
    void deleteTaskShouldDeleteItFromHistory() {
        int idTask1 = inMemoryTaskManager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50)));
        int idTask2 = inMemoryTaskManager.createTask(new Task("Task_2", "Task_2", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50)));
        int idEpic1 = inMemoryTaskManager.createEpic(new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50)));
        int idSubtask1Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1",
                0, Status.NEW, idEpic1, LocalDateTime.of(2024, 4, 10, 10, 45), Duration.ofMinutes(37)));
        int idSubtask2Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1",
                0, Status.NEW, idEpic1, LocalDateTime.of(2024, 4, 9, 11, 12), Duration.ofMinutes(70)));

        inMemoryTaskManager.getTaskById(idTask1);
        inMemoryTaskManager.getEpicById(idEpic1);
        inMemoryTaskManager.getSubTaskById(idSubtask1Epic1);
        inMemoryTaskManager.getSubTaskById(idSubtask2Epic1);

        final List<Task> history = inMemoryTaskManager.getHistory();
        List<Task> historyCompare = List.of(history.get(1), history.get(2), history.get(3));
        inMemoryTaskManager.deleteTaskById(idTask1);
        final List<Task> historyNew = inMemoryTaskManager.getHistory();
        assertEquals(historyCompare, historyNew, "После удаления задачи она не удалилась из истории.");
    }

    @Test
    void deleteEpicShouldDeleteEpicAndSubtasksFromHistory() {
        int idTask1 = inMemoryTaskManager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50)));
        int idTask2 = inMemoryTaskManager.createTask(new Task("Task_2", "Task_2", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50)));
        int idEpic1 = inMemoryTaskManager.createEpic(new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50)));
        int idSubtask1Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1",
                0, Status.NEW, idEpic1, LocalDateTime.of(2024, 4, 10, 10, 45), Duration.ofMinutes(37)));
        int idSubtask2Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1",
                0, Status.NEW, idEpic1, LocalDateTime.of(2024, 4, 9, 11, 12), Duration.ofMinutes(70)));

        inMemoryTaskManager.getTaskById(idTask1);
        inMemoryTaskManager.getEpicById(idEpic1);
        inMemoryTaskManager.getSubTaskById(idSubtask1Epic1);
        inMemoryTaskManager.getSubTaskById(idSubtask2Epic1);

        final List<Task> history = inMemoryTaskManager.getHistory();
        List<Task> historyCompare = List.of(history.get(0));
        inMemoryTaskManager.deleteEpicById(idEpic1);
        final List<Task> historyNew = inMemoryTaskManager.getHistory();
        assertEquals(historyCompare, historyNew, "После удаления эпика в истории остались связанные с ним подзадачи.");
    }
}