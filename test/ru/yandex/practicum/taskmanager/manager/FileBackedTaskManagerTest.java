package ru.yandex.practicum.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.tasks.Epic;
import ru.yandex.practicum.taskmanager.tasks.Status;
import ru.yandex.practicum.taskmanager.tasks.SubTask;
import ru.yandex.practicum.taskmanager.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File file;
    private FileBackedTaskManager manager;

    @BeforeEach
    void createFileAndManager() throws IOException {
        file = File.createTempFile("test", ".csv");
        manager = new FileBackedTaskManager(file);
    }

    @Test
    void saveEmptyFile() {
        manager.save();

        assertTrue(Files.exists(file.toPath()), "Файл не сохранился.");
    }

    @Test
    void loadEmptyFile() {
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(0, loadedManager.getListOfTasks().size(), "Количество задач не ноль.");
        assertEquals(0, loadedManager.getListOfSubTasks().size(), "Количество подзадач не ноль.");
        assertEquals(0, loadedManager.getListOfEpics().size(), "Количество эпиков не ноль.");
        assertEquals(0, loadedManager.getHistory().size(), "История не пустая.");
    }

    @Test
    void saveTasksToFile() throws IOException {
        int idTask1 = manager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW));
        int idEpic1 = manager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW));
        int idSubtask1Epic1 = manager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1", 0, Status.NEW, idEpic1));
        int idSubtask2Epic1 = manager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1", 0, Status.NEW, idEpic1));

        assertEquals(5, Files.lines(file.toPath()).count() - 1, "Количество сохраненных задач не совпадает.");
    }

    @Test
    void saveTasksAndHistoryToFile() throws IOException {
        int idTask1 = manager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW));
        int idEpic1 = manager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW));
        manager.getTaskById(idTask1);
        manager.getEpicById(idEpic1);

        assertEquals(4, Files.lines(file.toPath()).count() - 1, "Количество сохраненных задач и истории не совпадает.");
    }

    @Test
    void loadTasksAndHistoryFromFile() {
        int idTask1 = manager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW));
        int idEpic1 = manager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW));
        int idSubtask1Epic1 = manager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1", 0, Status.NEW, idEpic1));
        int idSubtask2Epic1 = manager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1", 0, Status.NEW, idEpic1));
        manager.getTaskById(idTask1);
        manager.getSubTaskById(idSubtask1Epic1);
        manager.getEpicById(idEpic1);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loadedManager.getListOfTasks().size(), "Количество загруженных задач не совпадает с количеством сохраненных.");
        assertEquals(1, loadedManager.getListOfEpics().size(), "Количество загруженных эпиков не совпадает с количеством сохраненных.");
        assertEquals(2, loadedManager.getListOfSubTasks().size(), "Количество загруженных подзадач не совпадает с количеством сохраненных.");
        assertEquals(3, loadedManager.getHistory().size(), "Количество задач в истории не совпадает с количеством просмотренных.");
    }

    @Test
    void checkThatSubtasksRestoredToEpicFromLoadedFile() {
        int idEpic1 = manager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW));
        int idSubtask1Epic1 = manager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1", 0, Status.NEW, idEpic1));
        int idSubtask2Epic1 = manager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1", 0, Status.NEW, idEpic1));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loadedManager.getEpicById(idEpic1).getSubtasksIds().contains(idSubtask1Epic1));
        assertTrue(loadedManager.getEpicById(idEpic1).getSubtasksIds().contains(idSubtask2Epic1));
    }
}