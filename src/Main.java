public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        System.out.println("Создаем две обычные задачи, один эпик с двумя подзадачами и один эпик с одной подзадачей");
        taskManager.createTask(new Task("Task_1_Title", "Task_1_Description", 0, Status.NEW));
        taskManager.createTask(new Task("Task_2_Title", "Task_2_Description", 0, Status.NEW));
        taskManager.createEpic(new Epic("Epic_1_Title", "Epic_1_Description", 0, Status.NEW));
        taskManager.createSubTask(3, new SubTask("SubTask_1_Title", "SubTask_1_Of_Epic_1", 0, Status.NEW));
        taskManager.createSubTask(3, new SubTask("SubTask_2_Title", "SubTask_2_Of_Epic_1", 0, Status.NEW));
        taskManager.createEpic(new Epic("Epic_2_Title", "Epic_2_Description", 0, Status.NEW));
        taskManager.createSubTask(6, new SubTask("SubTask_1_Title", "SubTask_1_Of_Epic_2", 0, Status.NEW));
        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println(taskManager.getListOfSubTasks());
        System.out.println();
        System.out.println("Меняем статусы обычных задач на IN_PROGRESS, статус одной подзадачи первого эпика на IN_PROGRESS, статус подзадачи второго эпика на IN_PROGRESS");
        taskManager.updateTask(new Task("Task_2_Title", "Task_2_Description", 1, Status.IN_PROGRESS));
        taskManager.updateTask(new Task("Task_2_Title", "Task_2_Description", 2, Status.IN_PROGRESS));
        taskManager.updateSubTask(3, new SubTask("SubTask_1_Title", "SubTask_1_Of_Epic_1", 4, Status.IN_PROGRESS));
        taskManager.updateSubTask(6, new SubTask("SubTask_1_Title", "SubTask_1_Of_Epic_2", 7, Status.IN_PROGRESS));
        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println(taskManager.getListOfSubTasks());
        System.out.println();
        System.out.println("Удаляем одну из задач и один из эпиков");
        taskManager.deleteTaskById(1);
        taskManager.deleteEpicById(6);
        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println(taskManager.getListOfSubTasks());
        System.out.println();
        System.out.println("Меняем статусы подзадач первого эпика на DONE, статус эпика поменяется на DONE");
        taskManager.updateSubTask(3, new SubTask("SubTask_1_Title", "SubTask_1_Of_Epic_1", 4, Status.DONE));
        taskManager.updateSubTask(3, new SubTask("SubTask_1_Title", "SubTask_1_Of_Epic_2", 5, Status.DONE));
        System.out.println(taskManager.getListOfEpics());
    }
}