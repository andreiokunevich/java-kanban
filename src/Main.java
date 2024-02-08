import ru.yandex.practicum.taskmanager.manager.*;
import ru.yandex.practicum.taskmanager.tasks.*;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        int idTask1 = taskManager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW));
        int idTask2 = taskManager.createTask(new Task("Task_2", "Task_2", 0, Status.NEW));
        int idEpic1 = taskManager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW));
        int idEpic2 = taskManager.createEpic(new Epic("Epic_2", "Epic_2", 0, Status.NEW));
        int idSubtask1Epic1 = taskManager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1", 0, Status.NEW, idEpic1));
        int idSubtask2Epic1 = taskManager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1", 0, Status.NEW, idEpic1));
        int idSubtask1Epic2 = taskManager.createSubTask(new SubTask("Subtask_1_Of_Epic_2", "Subtask_1_Of_Epic_2", 0, Status.NEW, idEpic2));

        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println(taskManager.getListOfSubTasks());

        Task task = taskManager.getTaskById(idTask1);
        task.setStatus(Status.DONE);
        taskManager.updateTask(task);

        SubTask subTask = taskManager.getSubTaskById(idSubtask1Epic1);
        subTask.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask);

        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println(taskManager.getListOfSubTasks());

        taskManager.deleteTaskById(idTask2);
        taskManager.deleteEpicById(idEpic2);

        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println(taskManager.getListOfSubTasks());
    }
}