package ru.yandex.practicum.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.tasks.Epic;
import ru.yandex.practicum.taskmanager.tasks.Status;
import ru.yandex.practicum.taskmanager.tasks.SubTask;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.util.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private TaskManager inMemoryTaskManager;

    @BeforeEach
    void createNewTaskManager() {
        inMemoryTaskManager = Managers.getInMemoryTaskManager();
    }

    @Test
    void getHistory() {
        Task task = new Task("Task", "Task", 0, Status.NEW);
        int taskId = inMemoryTaskManager.createTask(task);
        Task savedTaskNew = inMemoryTaskManager.getTaskById(taskId);

        final List<Task> history = inMemoryTaskManager.getHistory();

        assertNotNull(history, "Не удалось получить историю.");
    }

    @Test
    void historyShouldNotHasDuplicates() {
        int idTask1 = inMemoryTaskManager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW));
        int idTask2 = inMemoryTaskManager.createTask(new Task("Task_2", "Task_2", 0, Status.NEW));
        int idEpic1 = inMemoryTaskManager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW));
        int idSubtask1Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1", 0, Status.NEW, idEpic1));
        int idSubtask2Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1", 0, Status.NEW, idEpic1));

        inMemoryTaskManager.getTaskById(idTask1);
        inMemoryTaskManager.getEpicById(idEpic1);
        inMemoryTaskManager.getSubTaskById(idSubtask1Epic1);
        inMemoryTaskManager.getSubTaskById(idSubtask2Epic1);

        final List<Task> history = inMemoryTaskManager.getHistory();
        List<Task> historyCompare = List.of(history.get(1), history.get(2), history.get(3), history.get(0));
        inMemoryTaskManager.getTaskById(idTask1);
        final List<Task> historyNew = inMemoryTaskManager.getHistory();
        assertEquals(historyCompare, historyNew, "В истории появился дубликат.");
    }

    @Test
    void deleteTaskShouldDeleteItFromHistory() {
        int idTask1 = inMemoryTaskManager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW));
        int idTask2 = inMemoryTaskManager.createTask(new Task("Task_2", "Task_2", 0, Status.NEW));
        int idEpic1 = inMemoryTaskManager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW));
        int idSubtask1Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1", 0, Status.NEW, idEpic1));
        int idSubtask2Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1", 0, Status.NEW, idEpic1));

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
        int idTask1 = inMemoryTaskManager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW));
        int idTask2 = inMemoryTaskManager.createTask(new Task("Task_2", "Task_2", 0, Status.NEW));
        int idEpic1 = inMemoryTaskManager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW));
        int idSubtask1Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1", 0, Status.NEW, idEpic1));
        int idSubtask2Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1", 0, Status.NEW, idEpic1));

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