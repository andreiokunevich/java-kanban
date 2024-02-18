package ru.yandex.practicum.taskmanager.util;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.manager.HistoryManager;
import ru.yandex.practicum.taskmanager.manager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    public void checkInstancesOfManager() {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        assertNotNull(inMemoryTaskManager, "Не удалось создать объект Managers(inMemoryTaskManager).");

        HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
        assertNotNull(inMemoryHistoryManager, "Не удалось создать объект Managers(inMemoryHistoryManager).");
    }
}