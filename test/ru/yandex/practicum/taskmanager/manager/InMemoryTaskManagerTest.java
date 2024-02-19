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

class InMemoryTaskManagerTest {

    private TaskManager inMemoryTaskManager;

    @BeforeEach
    public void createNewTaskManager() {
        inMemoryTaskManager = Managers.getInMemoryTaskManager();
    }

    @Test
    public void addNewTask() {
        Task task = new Task("Task", "Task", 0, Status.NEW);
        final int taskId = inMemoryTaskManager.createTask(task);
        final Task savedTask = inMemoryTaskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = inMemoryTaskManager.getListOfTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

    }

    @Test
    public void addNewEpic() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW);
        final int epicId = inMemoryTaskManager.createEpic(epic);
        final Epic savedEpic = inMemoryTaskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = inMemoryTaskManager.getListOfEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    public void addNewSubtaskWithoutEpic() {
        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, 0);
        final int subTaskId = inMemoryTaskManager.createSubTask(subTask);
        assertEquals(-1, subTaskId, "Подзадача создалась без эпика.");
    }

    @Test
    public void addNewSubtask() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW);
        final int epicId = inMemoryTaskManager.createEpic(epic);

        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId);
        final int subTaskId = inMemoryTaskManager.createSubTask(subTask);
        final SubTask savedSubtask = inMemoryTaskManager.getSubTaskById(subTaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subTask, savedSubtask, "Подзадачи не совпадают.");

        final List<SubTask> subTasks = inMemoryTaskManager.getListOfSubTasks();

        assertNotNull(subTasks, "Подзадачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
        assertEquals(subTask, subTasks.get(0), "Подзадачи не совпадают.");

        final List<SubTask> subTasksOfEpic = inMemoryTaskManager.getAllSubTasksOfEpic(epicId);
        assertEquals(1, subTasksOfEpic.size(), "Подзадача не добавилась в эпик.");
    }

    @Test
    public void deleteOneTask() {
        int task1 = inMemoryTaskManager.createTask(new Task("Task1", "Task1", 0, Status.NEW));
        int task2 = inMemoryTaskManager.createTask(new Task("Task2", "Task2", 0, Status.NEW));

        assertEquals(2, inMemoryTaskManager.getListOfTasks().size(), "Задачи не добавились.");

        inMemoryTaskManager.deleteTaskById(task1);

        assertEquals(1, inMemoryTaskManager.getListOfTasks().size(), "Задача не удалилась.");
    }

    @Test
    public void deleteOneEpic() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW);
        final int epicId = inMemoryTaskManager.createEpic(epic);

        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId);
        final int subTaskId = inMemoryTaskManager.createSubTask(subTask);

        inMemoryTaskManager.deleteEpicById(epicId);

        assertEquals(0, inMemoryTaskManager.getListOfEpics().size(), "Эпик не удалился.");
        assertEquals(0, inMemoryTaskManager.getListOfSubTasks().size(), "Подзадача не удалилась вместе с эпиком.");
    }

    @Test
    public void deleteOneSubtask() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW);
        final int epicId = inMemoryTaskManager.createEpic(epic);

        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId);
        final int subTaskId = inMemoryTaskManager.createSubTask(subTask);

        inMemoryTaskManager.deleteSubTaskById(subTaskId);

        assertEquals(1, inMemoryTaskManager.getListOfEpics().size(), "При удалении подзадачи удалился его эпик.");
        assertEquals(0, inMemoryTaskManager.getAllSubTasksOfEpic(epicId).size(), "Подзадача не удалилась из эпика.");
        assertEquals(0, inMemoryTaskManager.getListOfSubTasks().size(), "Подзадача не удалилась.");
    }

    @Test
    public void deleteAllTasks() {
        int task1 = inMemoryTaskManager.createTask(new Task("Task1", "Task1", 0, Status.NEW));
        int task2 = inMemoryTaskManager.createTask(new Task("Task2", "Task2", 0, Status.NEW));

        inMemoryTaskManager.deleteAllTasks();

        assertEquals(0, inMemoryTaskManager.getListOfTasks().size(), "Задачи не удалились.");
    }

    @Test
    public void deleteAllSubtasks() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW);
        final int epicId = inMemoryTaskManager.createEpic(epic);

        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId);
        final int subTaskId = inMemoryTaskManager.createSubTask(subTask);
        SubTask subTask1 = new SubTask("Subtask1", "Subtask1", 0, Status.NEW, epicId);
        final int subTask1Id = inMemoryTaskManager.createSubTask(subTask1);

        inMemoryTaskManager.deleteAllSubtasks();

        assertEquals(1, inMemoryTaskManager.getListOfEpics().size(), "Эпик удалился вместе с подзадачами");
        assertEquals(0, inMemoryTaskManager.getListOfSubTasks().size(), "Подзадачи не удалились.");
        assertEquals(0, inMemoryTaskManager.getAllSubTasksOfEpic(epicId).size(), "Подзадачи не удалились из эпика.");
    }

    @Test
    public void deleteAllEpics() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW);
        final int epicId = inMemoryTaskManager.createEpic(epic);

        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId);
        final int subTaskId = inMemoryTaskManager.createSubTask(subTask);
        SubTask subTask1 = new SubTask("Subtask1", "Subtask1", 0, Status.NEW, epicId);
        final int subTask1Id = inMemoryTaskManager.createSubTask(subTask1);

        Epic epic1 = new Epic("Epic1", "Epic1", 0, Status.NEW);
        final int epic1Id = inMemoryTaskManager.createEpic(epic1);

        SubTask subTask2 = new SubTask("Subtask2", "Subtask2", 0, Status.NEW, epic1Id);
        final int subTas2Id = inMemoryTaskManager.createSubTask(subTask2);
        SubTask subTask3 = new SubTask("Subtask3", "Subtask3", 0, Status.NEW, epic1Id);
        final int subTask3Id = inMemoryTaskManager.createSubTask(subTask3);

        inMemoryTaskManager.deleteAllEpics();

        assertEquals(0, inMemoryTaskManager.getListOfEpics().size(), "Эпики не удалились.");
        assertEquals(0, inMemoryTaskManager.getListOfSubTasks().size(), "Подзадачи всех эпиков не удалились.");
    }

    @Test
    public void updateStatusOfTask() {
        Task task = new Task("Task", "Task", 0, Status.NEW);
        final int taskId = inMemoryTaskManager.createTask(task);
        Task task1 = inMemoryTaskManager.getTaskById(taskId);
        task1.setStatus(Status.DONE);
        inMemoryTaskManager.updateTask(task1);

        assertEquals(Status.DONE, inMemoryTaskManager.getTaskById(taskId).getStatus(), "Обновление статуса задачи не произошло.");
    }

    @Test
    public void shouldNotBeAbleToUpdateIdOfTask() {
        Task task = new Task("Task", "Task", 0, Status.NEW);
        final int taskId = inMemoryTaskManager.createTask(task);
        Task task1 = inMemoryTaskManager.getTaskById(taskId);
        task1.setId(5);
        inMemoryTaskManager.updateTask(task1);

        assertEquals(1, inMemoryTaskManager.getTaskById(taskId).getId(), "Произошло изменение ID задачи.");
    }

    @Test
    public void updateStatusOfSubtaskAndEpic() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW);
        final int epicId = inMemoryTaskManager.createEpic(epic);
        Epic epic1 = inMemoryTaskManager.getEpicById(epicId);
        epic1.setStatus(Status.DONE);
        inMemoryTaskManager.updateEpic(epic1);

        assertEquals(Status.NEW, inMemoryTaskManager.getEpicById(epicId).getStatus(), "Статус эпика изменился.");

        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId);
        final int subTaskId = inMemoryTaskManager.createSubTask(subTask);
        SubTask subTask1 = inMemoryTaskManager.getSubTaskById(subTaskId);
        subTask1.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateSubTask(subTask1);

        assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.getSubTaskById(subTaskId).getStatus(), "Статус подзадачи не изменился.");
        assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.getEpicById(epicId).getStatus(), "Статус эпика не изменился.");

        inMemoryTaskManager.deleteAllSubtasks();

        assertEquals(Status.NEW, inMemoryTaskManager.getEpicById(epicId).getStatus(), "Статус эпика не изменился после удаления подзадачи.");
    }

    @Test
    public void shouldNotBeAbleToUpdateIdOfSubtaskAndEpic() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW);
        final int epicId = inMemoryTaskManager.createEpic(epic);
        Epic epic1 = inMemoryTaskManager.getEpicById(epicId);
        epic1.setId(5);
        inMemoryTaskManager.updateEpic(epic1);

        assertEquals(1, inMemoryTaskManager.getEpicById(epicId).getId(), "Произошло изменение ID эпика.");

        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId);
        final int subTaskId = inMemoryTaskManager.createSubTask(subTask);
        SubTask subTask1 = inMemoryTaskManager.getSubTaskById(subTaskId);
        subTask1.setId(6);
        inMemoryTaskManager.updateSubTask(subTask1);

        assertEquals(2, inMemoryTaskManager.getSubTaskById(subTaskId).getId(), "Произошло изменение ID подзадачи.");
    }
}