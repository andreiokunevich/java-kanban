package ru.yandex.practicum.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.tasks.Epic;
import ru.yandex.practicum.taskmanager.tasks.Status;
import ru.yandex.practicum.taskmanager.tasks.SubTask;
import ru.yandex.practicum.taskmanager.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    public void createNewTaskManager() {
        inMemoryTaskManager = new InMemoryTaskManager();
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
    void addTasksToHistoryCheckVersionsOfTasks() {
        Task task = new Task("Task", "Task", 0, Status.NEW);
        int taskId = inMemoryTaskManager.createTask(task);
        Task savedTaskNew = inMemoryTaskManager.getTaskById(taskId);
        savedTaskNew.setStatus(Status.DONE);
        inMemoryTaskManager.updateTask(savedTaskNew);
        Task savedTaskDone = inMemoryTaskManager.getTaskById(taskId);

        assertEquals(2, inMemoryTaskManager.getHistory().size(), "Задачи не добавились в историю.");
        assertEquals(Status.NEW, inMemoryTaskManager.getHistory().get(0).getStatus(),
                "Изначально созданная задача не сохранилась в истории со старым статусом.");
        assertEquals(Status.DONE, inMemoryTaskManager.getHistory().get(1).getStatus(),
                "Измененная задача не добавилась в историю.");

        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW);
        final int epicId = inMemoryTaskManager.createEpic(epic);
        Epic epic1 = inMemoryTaskManager.getEpicById(epicId);

        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId);
        final int subTaskId = inMemoryTaskManager.createSubTask(subTask);
        SubTask subTask1 = inMemoryTaskManager.getSubTaskById(subTaskId);
        subTask1.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateSubTask(subTask1);
        SubTask subTask2 = inMemoryTaskManager.getSubTaskById(subTaskId);
        Epic epic2 = inMemoryTaskManager.getEpicById(epicId);

        assertEquals(6, inMemoryTaskManager.getHistory().size(), "Задачи не добавились в историю.");
        assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.getHistory().get(4).getStatus(),
                "Изначально созданная задача не сохранилась в истории со старым статусом.");
        assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.getHistory().get(5).getStatus(),
                "Измененная задача не добавилась в историю.");
    }

    @Test
    public void sizeOfHistoryCantBeMoreTenElements() {
        for (int i = 1; i < 15; i++) {
            Task task = new Task("Task", "Task", 0, Status.NEW);
            int taskId = inMemoryTaskManager.createTask(task);
            Task savedTaskNew = inMemoryTaskManager.getTaskById(taskId);
        }

        assertEquals(10, inMemoryTaskManager.getHistory().size(), "Размер истории больше возможного.");
    }
}