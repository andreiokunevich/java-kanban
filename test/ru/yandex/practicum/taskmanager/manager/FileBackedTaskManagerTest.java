package ru.yandex.practicum.taskmanager.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.tasks.Epic;
import ru.yandex.practicum.taskmanager.tasks.Status;
import ru.yandex.practicum.taskmanager.tasks.SubTask;
import ru.yandex.practicum.taskmanager.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private final File file = new File("test.csv");
    private FileBackedTaskManager manager;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return manager = new FileBackedTaskManager(file);
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
        int idTask1 = manager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50)));
        int idEpic1 = manager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 9, 10, 45),
                LocalDateTime.of(2024, 4, 10, 22, 30),
                Duration.ofMinutes(100)));
        int idSubtask1Epic1 = manager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1",
                0, Status.NEW, idEpic1, LocalDateTime.of(2024, 4, 10, 10, 45), Duration.ofMinutes(37)));
        int idSubtask2Epic1 = manager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1",
                0, Status.NEW, idEpic1, LocalDateTime.of(2024, 5, 10, 10, 45), Duration.ofMinutes(37)));

        assertEquals(5, Files.lines(file.toPath()).count() - 1, "Количество сохраненных задач не совпадает.");
    }


    @Test
    void loadTasksAndHistoryFromFile() {
        int idTask1 = manager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50)));
        int idEpic1 = manager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 9, 10, 45),
                LocalDateTime.of(2024, 4, 10, 22, 30), Duration.ofMinutes(100)));
        int idSubtask1Epic1 = manager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1", 0, Status.NEW, idEpic1,
                LocalDateTime.of(2024, 4, 10, 10, 45), Duration.ofMinutes(37)));
        int idSubtask2Epic1 = manager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1", 0, Status.NEW, idEpic1,
                LocalDateTime.of(2024, 5, 10, 10, 45), Duration.ofMinutes(37)));
        manager.getTaskById(idTask1);
        manager.getSubTaskById(idSubtask1Epic1);
        manager.getEpicById(idEpic1);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loadedManager.getListOfTasks().size(), "Количество загруженных задач не совпадает с количеством сохраненных.");
        assertEquals(1, loadedManager.getListOfEpics().size(), "Количество загруженных эпиков не совпадает с количеством сохраненных.");
        assertEquals(2, loadedManager.getListOfSubTasks().size(), "Количество загруженных подзадач не совпадает с количеством сохраненных.");
        assertEquals(3, loadedManager.getHistory().size(), "Количество задач в истории не совпадает с количеством просмотренных.");

        Task originalTask = manager.getTaskById(idTask1);
        Task fromFileTask = loadedManager.getTaskById(idTask1);

        assertEquals(originalTask, fromFileTask, "ID задачи не совпадает.");
        assertEquals(originalTask.getStatus(), fromFileTask.getStatus(), "Статус задачи не совпадает.");
        assertEquals(originalTask.getTitle(), fromFileTask.getTitle(), "Название задачи не совпадает.");
        assertEquals(originalTask.getDescription(), fromFileTask.getDescription(), "Описание задачи не совпадает.");

        SubTask originalSubtask = manager.getSubTaskById(idSubtask1Epic1);
        SubTask fromFileSubtask = loadedManager.getSubTaskById(idSubtask1Epic1);

        assertEquals(originalSubtask, fromFileSubtask, "ID подзадачи не совпадает.");
        assertEquals(originalSubtask.getStatus(), fromFileSubtask.getStatus(), "Статус подзадачи не совпадает.");
        assertEquals(originalSubtask.getTitle(), fromFileSubtask.getTitle(), "Название подзадачи не совпадает.");
        assertEquals(originalSubtask.getDescription(), fromFileSubtask.getDescription(), "Описание подзадачи не совпадает.");
        assertEquals(originalSubtask.getEpicId(), fromFileSubtask.getEpicId(), "ID эпика не совпадает");

        Epic originalEpic = manager.getEpicById(idEpic1);
        Epic fromFileEpic = loadedManager.getEpicById(idEpic1);

        assertEquals(originalEpic, fromFileEpic, "ID эпика не совпадает.");
        assertEquals(originalEpic.getStatus(), fromFileEpic.getStatus(), "Статус эпика не совпадает.");
        assertEquals(originalEpic.getTitle(), fromFileEpic.getTitle(), "Название эпика не совпадает.");
        assertEquals(originalEpic.getDescription(), fromFileEpic.getDescription(), "Описание эпика не совпадает.");
    }

    @Test
    void checkThatSubtasksRestoredToEpicFromLoadedFile() {
        int idEpic1 = manager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 9, 10, 45),
                LocalDateTime.of(2024, 4, 10, 22, 30), Duration.ofMinutes(100)));
        int idSubtask1Epic1 = manager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1", 0, Status.NEW, idEpic1,
                LocalDateTime.of(2024, 4, 10, 10, 45), Duration.ofMinutes(37)));
        int idSubtask2Epic1 = manager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1", 0, Status.NEW, idEpic1,
                LocalDateTime.of(2024, 5, 10, 10, 45), Duration.ofMinutes(37)));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loadedManager.getEpicById(idEpic1).getSubtasksIds().contains(idSubtask1Epic1));
        assertTrue(loadedManager.getEpicById(idEpic1).getSubtasksIds().contains(idSubtask2Epic1));
    }

    @Test
    void checkThatMaximumIdRestoredFromFile() {
        int idTask1 = manager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50)));
        int idEpic1 = manager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 9, 10, 45),
                LocalDateTime.of(2024, 4, 10, 22, 30), Duration.ofMinutes(100)));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        int idNewTask = loadedManager.createTask(new Task("Task_2", "Task_2", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50)));

        assertEquals(3, idNewTask);
    }

    @Test
    void testSaveToFile() {
        Task task = new Task("Task_1", "Task_1", 0, Status.NEW,
                LocalDateTime.of(2024, 4, 8, 23, 0), Duration.ofMinutes(50));

        assertDoesNotThrow(() -> manager.createTask(task), "Сохранение в файл не должно выбросить исключение.");
    }

    @Test
    void testLoadFromFile() {
        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(file), "При загрузке из файла не должно выброситься исключение.");
    }
}