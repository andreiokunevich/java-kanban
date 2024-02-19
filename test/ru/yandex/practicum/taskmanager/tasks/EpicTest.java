package ru.yandex.practicum.taskmanager.tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.manager.TaskManager;
import ru.yandex.practicum.taskmanager.util.Managers;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EpicTest {

    private TaskManager inMemoryTaskManager;

    @BeforeEach
    public void createNewTaskManager() {
        inMemoryTaskManager = Managers.getInMemoryTaskManager();
    }

    @Test
    public void cantAddEpicAsOwnSubtask() {
        int idEpic = inMemoryTaskManager.createEpic(new Epic("Epic", "Epic", 0, Status.NEW));
        inMemoryTaskManager.getEpicById(idEpic).addSubtaskId(idEpic);
        Assertions.assertFalse(inMemoryTaskManager.getEpicById(idEpic).getSubtasksIds().contains(idEpic));
    }

    @Test
    void checkEqualityOfEpics() {
        Epic epic1 = new Epic("Epic", "Epic", 0, Status.NEW);
        final int epic1Id = inMemoryTaskManager.createEpic(epic1);
        Epic epic2 = new Epic("Epic", "Epic", 0, Status.NEW);
        final int epic2Id = inMemoryTaskManager.createEpic(epic2);

        assertNotEquals(epic1, epic2, "Эпики с разными ID, но одинаковым содержимым оказались равны.");
    }
}