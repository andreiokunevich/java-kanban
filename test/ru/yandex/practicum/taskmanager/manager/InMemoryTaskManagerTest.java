package ru.yandex.practicum.taskmanager.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.tasks.Epic;
import ru.yandex.practicum.taskmanager.tasks.Status;
import ru.yandex.practicum.taskmanager.tasks.SubTask;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return manager = Managers.getInMemoryTaskManager();
    }

    @Test
    public void addNewSubtaskWithoutEpic() {
        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, 0,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int subTaskId = manager.createSubTask(subTask);
        assertEquals(-1, subTaskId, "Подзадача создалась без эпика.");
    }

    @Test
    public void updateStatusOfTask() {
        Task task = new Task("Task_1", "Task_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int taskId = manager.createTask(task);
        Task task1 = manager.getTaskById(taskId);
        task1.setStatus(Status.DONE);
        manager.updateTask(task1);

        assertEquals(Status.DONE, manager.getTaskById(taskId).getStatus(), "Обновление статуса задачи не произошло.");
    }

    @Test
    public void shouldNotBeAbleToUpdateIdOfTask() {
        Task task = new Task("Task_1", "Task_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int taskId = manager.createTask(task);
        Task task1 = manager.getTaskById(taskId);
        task1.setId(5);
        manager.updateTask(task1);

        assertEquals(1, manager.getTaskById(taskId).getId(), "Произошло изменение ID задачи.");
    }

    @Test
    public void updateStatusOfSubtaskAndEpic() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        final int epicId = manager.createEpic(epic);
        Epic epic1 = manager.getEpicById(epicId);
        epic1.setStatus(Status.DONE);
        manager.updateEpic(epic1);

        assertEquals(Status.NEW, manager.getEpicById(epicId).getStatus(), "Статус эпика изменился.");

        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int subTaskId = manager.createSubTask(subTask);
        SubTask subTask1 = manager.getSubTaskById(subTaskId);
        subTask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subTask1);

        assertEquals(Status.IN_PROGRESS, manager.getSubTaskById(subTaskId).getStatus(), "Статус подзадачи не изменился.");
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epicId).getStatus(), "Статус эпика не изменился.");

        manager.deleteAllSubtasks();

        assertEquals(Status.NEW, manager.getEpicById(epicId).getStatus(), "Статус эпика не изменился после удаления подзадачи.");
    }

    @Test
    public void shouldNotBeAbleToUpdateIdOfSubtaskAndEpic() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        final int epicId = manager.createEpic(epic);
        Epic epic1 = manager.getEpicById(epicId);
        epic1.setId(5);
        manager.updateEpic(epic1);

        assertEquals(1, manager.getEpicById(epicId).getId(), "Произошло изменение ID эпика.");

        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int subTaskId = manager.createSubTask(subTask);
        SubTask subTask1 = manager.getSubTaskById(subTaskId);
        subTask1.setId(6);
        manager.updateSubTask(subTask1);

        assertEquals(2, manager.getSubTaskById(subTaskId).getId(), "Произошло изменение ID подзадачи.");
    }

    @Test
    void testUpdateOfEpicsDurationAndTimes() {
        int idEpic1 = manager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW,
                LocalDateTime.of(2024, 6, 9, 10, 45),
                LocalDateTime.of(2024, 7, 10, 22, 30), Duration.ofMinutes(20)));
        int idSubtask1Epic1 = manager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1",
                0, Status.NEW, idEpic1, LocalDateTime.of(2024, 4, 18, 10, 45), Duration.ofMinutes(30)));

        assertEquals(30, manager.getEpicById(idEpic1).getDuration().toMinutes(),
                "Время продолжительности эпика рассчитано неверно.");
        assertEquals(manager.getEpicById(idEpic1).getStartTime(), manager.getSubTaskById(idSubtask1Epic1).getStartTime(),
                "Дата и время старта эпика неверные.");
        assertEquals(manager.getEpicById(idEpic1).getEndTime(), manager.getSubTaskById(idSubtask1Epic1).getEndTime(),
                "Дата и время завершения эпика неверные.");

        int idSubtask2Epic1 = manager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1",
                0, Status.NEW, idEpic1, LocalDateTime.of(2024, 4, 13, 11, 12), Duration.ofMinutes(60)));
        int idSubtask3Epic1 = manager.createSubTask(new SubTask("Subtask_3_Of_Epic_1", "Subtask_3_Of_Epic_1",
                0, Status.NEW, idEpic1, LocalDateTime.of(2024, 4, 15, 17, 15), Duration.ofMinutes(10)));

        assertEquals(100, manager.getEpicById(idEpic1).getDuration().toMinutes(),
                "Время продолжительности эпика рассчитано неверно.");
        assertEquals(manager.getEpicById(idEpic1).getStartTime(), manager.getSubTaskById(idSubtask2Epic1).getStartTime(),
                "Дата и время старта эпика неверные.");
        assertEquals(manager.getEpicById(idEpic1).getEndTime(), manager.getSubTaskById(idSubtask1Epic1).getEndTime(),
                "Дата и время завершения эпика неверные.");

        int idSubtask4Epic1 = manager.createSubTask(new SubTask("Subtask_3_Of_Epic_1", "Subtask_3_Of_Epic_1",
                0, Status.NEW, idEpic1, null, Duration.ofMinutes(10)));

        assertEquals(100, manager.getEpicById(idEpic1).getDuration().toMinutes(),
                "Время продолжительности эпика изменилось после добавления подзадачи без времени начала.");
        assertEquals(manager.getEpicById(idEpic1).getStartTime(), manager.getSubTaskById(idSubtask2Epic1).getStartTime(),
                "Дата и время старта эпика изменились после добавления подзадачи без времени начала.");
        assertEquals(manager.getEpicById(idEpic1).getEndTime(), manager.getSubTaskById(idSubtask1Epic1).getEndTime(),
                "Дата и время завершения эпика изменились после добавления подзадачи без времени начала.");
    }
}