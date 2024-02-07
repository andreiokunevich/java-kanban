public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        taskManager.createTask(new Task("Task_1", "Task_1", 0, Status.NEW));
        taskManager.createTask(new Task("Task_2", "Task_2", 0, Status.NEW));
        taskManager.createEpic(new Epic("Epic_1", "Epic_1", 0, Status.NEW));
        taskManager.createEpic(new Epic("Epic_2", "Epic_2", 0, Status.NEW));
        taskManager.createSubTask(new SubTask("Subtask_1_Of_Epic_1", "Subtask_1_Of_Epic_1", 0, Status.NEW, 3));
        taskManager.createSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1", 0, Status.NEW, 3));
        taskManager.createSubTask(new SubTask("Subtask_1_Of_Epic_2", "Subtask_1_Of_Epic_2", 0, Status.NEW, 4));
        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println(taskManager.getListOfSubTasks());
        taskManager.updateTask(new Task("Task_1", "Task_1", 1, Status.IN_PROGRESS));
        taskManager.updateSubTask(new SubTask("Subtask_2_Of_Epic_1", "Subtask_2_Of_Epic_1", 6, Status.IN_PROGRESS, 3));
        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println(taskManager.getListOfSubTasks());
        taskManager.deleteTaskById(2);
        taskManager.deleteEpicById(3);
        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println(taskManager.getListOfSubTasks());
    }
}