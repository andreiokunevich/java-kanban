import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private static int id = 1;

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, SubTask> subtasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, ArrayList<Integer>> epicSubtasks = new HashMap<>();

    public static int getId() {
        return id;
    }

    public static int generateNewId() {
        return id++;
    }

    public void createTask(Task task) {
        int id = generateNewId();
        task.setId(id);
        tasks.put(id, task);
    }

    public void createSubTask(int idEpic, SubTask subTask) {
        if (epics.containsKey(idEpic)) {
            int id = generateNewId();
            subTask.setId(id);
            subtasks.put(id, subTask);
            addSubTaskToEpic(idEpic, subTask.getId());
        } else {
            System.out.println("Эпик не найден");
        }
    }

    public void createEpic(Epic epic) {
        int id = generateNewId();
        epic.setId(id);
        epics.put(id, epic);
    }

    public ArrayList<Task> getListOfTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<SubTask> getListOfSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getListOfEpics() {
        return new ArrayList<>(epics.values());
    }

    public Task getTaskById(int id) {
        Task taskById = null;
        for (Integer task : tasks.keySet()) {
            if (task == id) {
                taskById = tasks.get(id);
            }
        }
        return taskById;
    }

    public SubTask getSubTaskById(int id) {
        SubTask subTaskById = null;
        for (Integer task : subtasks.keySet()) {
            if (task == id) {
                subTaskById = subtasks.get(id);
            }
        }
        return subTaskById;
    }

    public Epic getEpicById(int id) {
        Epic epicById = null;
        for (Integer task : epics.keySet()) {
            if (task == id) {
                epicById = epics.get(id);
            }
        }
        return epicById;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubTask(int idEpic, SubTask subTask) {
        ArrayList<Integer> subs = epicSubtasks.get(idEpic);
        if (subs.contains(subTask.getId())) {
            subtasks.put(subTask.getId(), subTask);
            updateEpic(idEpic);
        } else {
            System.out.println("Неверно указан эпик");
        }
    }

    public void updateEpic(int idEpic) {
        ArrayList<SubTask> subs = getAllSubTasksOfEpic(idEpic);
        if (isNew(subs)) {
            epics.get(idEpic).setStatus(Status.NEW);
        } else if (isDone(subs)) {
            epics.get(idEpic).setStatus(Status.DONE);
        } else {
            epics.get(idEpic).setStatus(Status.IN_PROGRESS);
        }
    }

    public boolean isNew(ArrayList<SubTask> subs) {
        boolean isAllNew = true;
        for (SubTask sub : subs) {
            if (sub.getStatus() != Status.NEW) {
                isAllNew = false;
                break;
            }
        }
        return isAllNew;
    }

    public boolean isDone(ArrayList<SubTask> subs) {
        boolean isDone = true;
        for (SubTask sub : subs) {
            if (sub.getStatus() != Status.DONE) {
                isDone = false;
                break;
            }
        }
        return isDone;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        for (ArrayList<Integer> subs : epicSubtasks.values()) {
            subs.clear();
        }
        subtasks.clear();
    }

    public void deleteAllEpics() {
        epicSubtasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteSubTaskById(Integer id) {
        for (ArrayList<Integer> idSubs : epicSubtasks.values()) {
            if (idSubs.contains(id)) {
                idSubs.remove(id);
                break;
            }
        }
        subtasks.remove(id);
    }

    public void deleteEpicById(int id) {
        ArrayList<Integer> idSubs = epicSubtasks.get(id);
        for (Integer idSub : idSubs) {
            subtasks.remove(idSub);
        }
        epicSubtasks.remove(id);
        epics.remove(id);
    }

    public void addSubTaskToEpic(int idEpic, int idSubTask) {
        ArrayList<Integer> subs;
        if (epicSubtasks.containsKey(idEpic)) {
            subs = epicSubtasks.get(idEpic);
            subs.add(idSubTask);
            epicSubtasks.put(idEpic, subs);
        } else {
            subs = new ArrayList<>();
            subs.add(idSubTask);
            epicSubtasks.put(idEpic, subs);
        }
    }

    public ArrayList<SubTask> getAllSubTasksOfEpic(int idEpic) {
        ArrayList<Integer> idSubs = epicSubtasks.get(idEpic);
        ArrayList<SubTask> subTasks = new ArrayList<>();
        for (Integer idSub : idSubs) {
            if (subtasks.containsKey(idSub)) {
                subTasks.add(subtasks.get(idSub));
            }
        }
        return subTasks;
    }
}