package ru.yandex.practicum.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.tasks.Epic;
import ru.yandex.practicum.taskmanager.tasks.Status;
import ru.yandex.practicum.taskmanager.tasks.SubTask;
import ru.yandex.practicum.taskmanager.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected abstract T createTaskManager();

    protected T manager;

    @BeforeEach
    void create() {
        manager = createTaskManager();
    }

    @Test
    void testCreateTask() {
        Task task = new Task("Task_1", "Task_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int taskId = manager.createTask(task);
        final Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getListOfTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void testCreateSubTask() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        final int epicId = manager.createEpic(epic);

        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int subTaskId = manager.createSubTask(subTask);
        final SubTask savedSubtask = manager.getSubTaskById(subTaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subTask, savedSubtask, "Подзадачи не совпадают.");

        final List<SubTask> subTasks = manager.getListOfSubTasks();

        assertNotNull(subTasks, "Подзадачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
        assertEquals(subTask, subTasks.get(0), "Подзадачи не совпадают.");

        final List<SubTask> subTasksOfEpic = manager.getAllSubTasksOfEpic(epicId);
        assertEquals(1, subTasksOfEpic.size(), "Подзадача не добавилась в эпик.");
    }

    @Test
    void testCreateEpic() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        final int epicId = manager.createEpic(epic);
        final Epic savedEpic = manager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = manager.getListOfEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void testGetListOfTasks() {
        manager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50)));

        assertNotNull(manager.getListOfTasks(), "Лист задач не получен.");
        assertEquals(1, manager.getListOfTasks().size(), "Лист задач пустой.");
    }

    @Test
    void testGetListOfSubTasks() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        final int epicId = manager.createEpic(epic);

        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int subTaskId = manager.createSubTask(subTask);

        assertNotNull(manager.getListOfSubTasks(), "Лист подзадач не получен.");
        assertEquals(1, manager.getListOfSubTasks().size(), "Лист подзадач пустой.");
    }

    @Test
    void testGetListOfEpics() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        final int epicId = manager.createEpic(epic);

        assertNotNull(manager.getListOfEpics(), "Лист подзадач не получен.");
        assertEquals(1, manager.getListOfEpics().size(), "Лист подзадач пустой.");
    }

    @Test
    void testGetTaskById() {
        int task1 = manager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50)));
        Task taskFromManager = manager.getTaskById(task1);

        assertNotNull(taskFromManager, "Задача не получена из менеджера.");
    }

    @Test
    void testGetSubTaskById() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        final int epicId = manager.createEpic(epic);

        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int subTaskId = manager.createSubTask(subTask);
        SubTask subTaskFromManager = manager.getSubTaskById(subTaskId);

        assertNotNull(subTaskFromManager, "Подзадача не получена из менеджера.");

    }

    @Test
    void testGetEpicById() {
        int epicid = manager.createEpic(new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50)));
        Epic epicFromManager = manager.getEpicById(epicid);

        assertNotNull(epicFromManager, "Эпик не получен из менеджера.");
    }

    @Test
    void testDeleteAllTasks() {
        int task1 = manager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50)));
        int task2 = manager.createTask(new Task("Task_2", "Task_2", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50)));

        manager.deleteAllTasks();

        assertEquals(0, manager.getListOfTasks().size(), "Задачи не удалились.");
    }

    @Test
    void testDeleteAllSubtasks() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        final int epicId = manager.createEpic(epic);

        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int subTaskId = manager.createSubTask(subTask);
        SubTask subTask1 = new SubTask("Subtask1", "Subtask1", 0, Status.NEW, epicId,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int subTask1Id = manager.createSubTask(subTask1);

        manager.deleteAllSubtasks();

        assertEquals(1, manager.getListOfEpics().size(), "Эпик удалился вместе с подзадачами");
        assertEquals(0, manager.getListOfSubTasks().size(), "Подзадачи не удалились.");
        assertEquals(0, manager.getAllSubTasksOfEpic(epicId).size(), "Подзадачи не удалились из эпика.");
    }

    @Test
    void testDeleteAllEpics() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        final int epicId = manager.createEpic(epic);

        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int subTaskId = manager.createSubTask(subTask);
        SubTask subTask1 = new SubTask("Subtask1", "Subtask1", 0, Status.NEW, epicId,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int subTask1Id = manager.createSubTask(subTask1);

        Epic epic1 = new Epic("Epic1", "Epic1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        final int epic1Id = manager.createEpic(epic1);

        SubTask subTask2 = new SubTask("Subtask2", "Subtask2", 0, Status.NEW, epicId,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int subTas2Id = manager.createSubTask(subTask2);
        SubTask subTask3 = new SubTask("Subtask3", "Subtask3", 0, Status.NEW, epicId,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int subTask3Id = manager.createSubTask(subTask3);

        manager.deleteAllEpics();

        assertEquals(0, manager.getListOfEpics().size(), "Эпики не удалились.");
        assertEquals(0, manager.getListOfSubTasks().size(), "Подзадачи всех эпиков не удалились.");
    }

    @Test
    void testDeleteTaskById() {
        int task1 = manager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50)));
        int task2 = manager.createTask(new Task("Task_2", "Task_2", 0, Status.NEW,
                LocalDateTime.of(2024, 5, 8, 23, 0), Duration.ofMinutes(50)));

        assertEquals(2, manager.getListOfTasks().size(), "Задачи не добавились.");

        manager.deleteTaskById(task1);

        assertEquals(1, manager.getListOfTasks().size(), "Задача не удалилась.");
    }

    @Test
    void testDeleteSubTaskById() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        final int epicId = manager.createEpic(epic);

        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int subTaskId = manager.createSubTask(subTask);

        manager.deleteSubTaskById(subTaskId);

        assertEquals(1, manager.getListOfEpics().size(), "При удалении подзадачи удалился его эпик.");
        assertEquals(0, manager.getAllSubTasksOfEpic(epicId).size(), "Подзадача не удалилась из эпика.");
        assertEquals(0, manager.getListOfSubTasks().size(), "Подзадача не удалилась.");
    }

    @Test
    void testDeleteEpicById() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        final int epicId = manager.createEpic(epic);

        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int subTaskId = manager.createSubTask(subTask);

        manager.deleteEpicById(epicId);

        assertEquals(0, manager.getListOfEpics().size(), "Эпик не удалился.");
        assertEquals(0, manager.getListOfSubTasks().size(), "Подзадача не удалилась вместе с эпиком.");
    }

    @Test
    void testGetAllSubTasksOfEpic() {
        Epic epic = new Epic("Epic", "Epic", 1, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        final int epicId = manager.createEpic(epic);
        SubTask subTask = new SubTask("Subtask", "Subtask", 2, Status.NEW, epicId,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("Subtask2", "Subtask2", 3, Status.NEW, epicId,
                LocalDateTime.of(2024, 4, 9, 23, 0), Duration.ofMinutes(50));
        manager.createSubTask(subTask2);

        assertNotNull(manager.getAllSubTasksOfEpic(epicId), "Лист подзадач эпика не получен.");
        assertEquals(2, manager.getAllSubTasksOfEpic(epicId).size(), "Лист подзадач эпика не совпадает.");
    }

    @Test
    void testUpdateTask() {
        int task1 = manager.createTask(new Task("Task_1", "Task_1", 1, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50)));
        Task taskUpdate = new Task("Task_1_Update", "Task_1", 1, Status.IN_PROGRESS,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        manager.updateTask(taskUpdate);

        assertEquals(taskUpdate, manager.getTaskById(task1), "Задача не обновилась.");
    }

    @Test
    void testUpdateSubTask() {
        Epic epic = new Epic("Epic", "Epic", 1, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        final int epicId = manager.createEpic(epic);

        SubTask subTask = new SubTask("Subtask", "Subtask", 2, Status.NEW, epicId,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int subTaskId = manager.createSubTask(subTask);
        SubTask subTaskUpdate = new SubTask("Subtask_Update", "Subtask", 2, Status.IN_PROGRESS, epicId,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        manager.updateSubTask(subTaskUpdate);

        assertEquals(subTaskUpdate, manager.getSubTaskById(subTaskId), "Подзадача не обновилась.");
    }

    @Test
    void testUpdateEpic() {
        Epic epic = new Epic("Epic", "Epic", 1, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        final int epicId = manager.createEpic(epic);
        Epic epicUpdate = new Epic("Epic_Update", "Epic", 1, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        manager.updateEpic(epicUpdate);

        assertEquals(epicUpdate, manager.getEpicById(epicId), "Эпик не обновился.");
    }

    @Test
    void testGetHistory() {
        assertNotNull(manager.getHistory());
    }

    @Test
    void testGetPrioritizedTasks() {
        assertNotNull(manager.getPrioritizedTasks());
    }

    @Test
    void checkIntersection() {
        int idTask1 = manager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 40), Duration.ofMinutes(50)));
        int idTask2 = manager.createTask(new Task("Task_2", "Task_2", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 55), Duration.ofMinutes(15)));

        assertEquals(1, manager.getListOfTasks().size(), "Задачи пересеклись по времени.");
        assertFalse(manager.getListOfTasks().contains(manager.getTaskById(idTask2)));

    }
}