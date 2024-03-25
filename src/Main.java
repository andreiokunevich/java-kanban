import ru.yandex.practicum.taskmanager.manager.*;
import ru.yandex.practicum.taskmanager.tasks.*;

import java.io.File;

public class Main {

    private static void printHistory(TaskManager manager) {
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getListOfTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getListOfEpics()) {
            System.out.println(epic);

            for (Task task : manager.getAllSubTasksOfEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getListOfSubTasks()) {
            System.out.println(subtask);
        }

        printHistory(manager);
    }

    public static void main(String[] args) {
        TaskManager manager = new FileBackedTaskManager(new File("data.csv"));

        int idTask1 = manager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW));
        int idTask2 = manager.createTask(new Task("Task_2", "Task_2", 0, Status.NEW));
        int idEpic1 = manager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW));
        int idEpic2 = manager.createEpic(new Epic("Epic_2", "Epic_2", 0, Status.NEW));
        int idSubtask1Epic1 = manager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1", 0, Status.NEW, idEpic1));
        int idSubtask2Epic1 = manager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1", 0, Status.NEW, idEpic1));
        int idSubtask3Epic1 = manager.createSubTask(new SubTask("Subtask_3_Of_Epic_1", "Subtask_3_Of_Epic_1", 0, Status.NEW, idEpic1));

        manager.getTaskById(idTask2);
        manager.getSubTaskById(idSubtask2Epic1);
        manager.getEpicById(idEpic2);
        printAllTasks(manager);

        System.out.println("\nПОСЛЕ ВОССТАНОВЛЕНИЯ ИЗ ФАЙЛА");

        TaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(new File("data.csv"));
        printAllTasks(fileBackedTaskManager);

        System.out.println("\nПРОДОЛЖАЕМ РАБОТУ С ПОЛУЧЕННЫМ МЕНЕДЖЕРОМ");

        fileBackedTaskManager.getTaskById(idTask1);
        fileBackedTaskManager.getTaskById(idTask2);
        fileBackedTaskManager.getSubTaskById(idSubtask1Epic1);
        printAllTasks(fileBackedTaskManager);
    }
}