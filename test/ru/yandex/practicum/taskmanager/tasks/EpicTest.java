package ru.yandex.practicum.taskmanager.tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.manager.TaskManager;
import ru.yandex.practicum.taskmanager.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EpicTest {

    private TaskManager inMemoryTaskManager;

    @BeforeEach
    public void createNewTaskManager() {
        inMemoryTaskManager = Managers.getInMemoryTaskManager();
    }

    @Test
    public void cantAddEpicAsOwnSubtask() {
        int idEpic = inMemoryTaskManager.createEpic(new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50)));
        inMemoryTaskManager.getEpicById(idEpic).addSubtaskId(idEpic);
        Assertions.assertFalse(inMemoryTaskManager.getEpicById(idEpic).getSubtasksIds().contains(idEpic));
    }

    @Test
    void checkEqualityOfEpics() {
        Epic epic1 = new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        final int epic1Id = inMemoryTaskManager.createEpic(epic1);
        Epic epic2 = new Epic("Epic", "Epic", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0),
                LocalDateTime.of(2024, 4, 8, 23, 50), Duration.ofMinutes(50));
        final int epic2Id = inMemoryTaskManager.createEpic(epic2);

        assertNotEquals(epic1, epic2, "Эпики с разными ID, но одинаковым содержимым оказались равны.");
    }

    @Test
    void checkStatusOfEpicAllSubtasksAreNEW() {
        int idEpic1 = inMemoryTaskManager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW,
                LocalDateTime.of(2024, 6, 9, 10, 45),
                LocalDateTime.of(2024, 7, 10, 22, 30), Duration.ofMinutes(20)));
        int idSubtask1Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1",
                0, Status.NEW, idEpic1, LocalDateTime.of(2024, 4, 18, 10, 45), Duration.ofMinutes(30)));
        int idSubtask2Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1",
                0, Status.NEW, idEpic1, LocalDateTime.of(2024, 4, 13, 11, 12), Duration.ofMinutes(60)));
        int idSubtask3Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_3_Of_Epic_1", "Subtask_3_Of_Epic_1",
                0, Status.NEW, idEpic1, LocalDateTime.of(2024, 4, 15, 17, 15), Duration.ofMinutes(10)));

        assertEquals(Status.NEW, inMemoryTaskManager.getEpicById(idEpic1).getStatus(), "Статус эпика не NEW.");
    }

    @Test
    void checkStatusOfEpicAllSubtasksAreDONE() {
        int idEpic1 = inMemoryTaskManager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW,
                LocalDateTime.of(2024, 6, 9, 10, 45),
                LocalDateTime.of(2024, 7, 10, 22, 30), Duration.ofMinutes(20)));
        int idSubtask1Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1",
                0, Status.DONE, idEpic1, LocalDateTime.of(2024, 4, 18, 10, 45), Duration.ofMinutes(30)));
        int idSubtask2Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1",
                0, Status.DONE, idEpic1, LocalDateTime.of(2024, 4, 13, 11, 12), Duration.ofMinutes(60)));
        int idSubtask3Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_3_Of_Epic_1", "Subtask_3_Of_Epic_1",
                0, Status.DONE, idEpic1, LocalDateTime.of(2024, 4, 15, 17, 15), Duration.ofMinutes(10)));

        assertEquals(Status.DONE, inMemoryTaskManager.getEpicById(idEpic1).getStatus(), "Статус эпика не DONE.");
    }

    @Test
    void checkStatusOfEpicSubtasksAreNEWorDONE() {
        int idEpic1 = inMemoryTaskManager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW,
                LocalDateTime.of(2024, 6, 9, 10, 45),
                LocalDateTime.of(2024, 7, 10, 22, 30), Duration.ofMinutes(20)));
        int idSubtask1Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1",
                0, Status.NEW, idEpic1, LocalDateTime.of(2024, 4, 18, 10, 45), Duration.ofMinutes(30)));
        int idSubtask2Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1",
                0, Status.DONE, idEpic1, LocalDateTime.of(2024, 4, 13, 11, 12), Duration.ofMinutes(60)));
        int idSubtask3Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_3_Of_Epic_1", "Subtask_3_Of_Epic_1",
                0, Status.DONE, idEpic1, LocalDateTime.of(2024, 4, 15, 17, 15), Duration.ofMinutes(10)));

        assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.getEpicById(idEpic1).getStatus(), "Статус эпика не IN_PROGRESS.");
    }

    @Test
    void checkStatusOfEpicSubtasksAreINPROGRESS() {
        int idEpic1 = inMemoryTaskManager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW,
                LocalDateTime.of(2024, 6, 9, 10, 45),
                LocalDateTime.of(2024, 7, 10, 22, 30), Duration.ofMinutes(20)));
        int idSubtask1Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1",
                0, Status.IN_PROGRESS, idEpic1, LocalDateTime.of(2024, 4, 18, 10, 45), Duration.ofMinutes(30)));
        int idSubtask2Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1",
                0, Status.IN_PROGRESS, idEpic1, LocalDateTime.of(2024, 4, 13, 11, 12), Duration.ofMinutes(60)));
        int idSubtask3Epic1 = inMemoryTaskManager.createSubTask(new SubTask("Subtask_3_Of_Epic_1", "Subtask_3_Of_Epic_1",
                0, Status.IN_PROGRESS, idEpic1, LocalDateTime.of(2024, 4, 15, 17, 15), Duration.ofMinutes(10)));

        assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.getEpicById(idEpic1).getStatus(), "Статус эпика не IN_PROGRESS.");
    }
}