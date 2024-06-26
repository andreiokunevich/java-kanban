package ru.yandex.practicum.taskmanager.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.manager.TaskManager;
import ru.yandex.practicum.taskmanager.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    private TaskManager inMemoryTaskManager;

    @BeforeEach
    public void createNewTaskManager() {
        inMemoryTaskManager = Managers.getInMemoryTaskManager();
    }

    @Test
    public void cantAddSubtaskAsOwnEpic() {
        int idEpic = inMemoryTaskManager.createEpic(new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50)));
        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, idEpic,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        int subTaskId = inMemoryTaskManager.createSubTask(subTask);

        SubTask savedSubtask = inMemoryTaskManager.getSubTaskById(subTaskId);

        SubTask subTask1 = new SubTask(savedSubtask.getTitle(), savedSubtask.getDescription(), savedSubtask.getId(),
                savedSubtask.getStatus(), subTaskId, savedSubtask.getStartTime(), savedSubtask.getDuration());
        inMemoryTaskManager.updateSubTask(subTask);

        assertNotEquals(subTask.getEpicId(), subTask.getId(), "Подзадача добавилась как свой эпик.");
    }

    @Test
    void checkEqualityOfSubtasks() {
        Epic epic = new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        final int epicId = inMemoryTaskManager.createEpic(epic);

        SubTask subTask = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int subTaskId = inMemoryTaskManager.createSubTask(subTask);
        SubTask subTask1 = new SubTask("Subtask", "Subtask", 0, Status.NEW, epicId,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));
        final int subTask1Id = inMemoryTaskManager.createSubTask(subTask1);

        assertNotEquals(subTask, subTask1, "Подзадачи с разными ID, но одинаковым содержимым оказались равны.");
    }
}