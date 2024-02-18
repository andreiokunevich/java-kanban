package ru.yandex.practicum.taskmanager.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.yandex.practicum.taskmanager.tasks.Epic;
import ru.yandex.practicum.taskmanager.tasks.Status;
import ru.yandex.practicum.taskmanager.tasks.SubTask;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.util.Managers;

public class InMemoryTaskManager implements TaskManager {

    private int id = 1;

    private final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();


    private int generateNewId() {
        return id++;
    }

    @Override
    public int createTask(Task task) {
        int id = generateNewId();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public int createSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicId())) {
            int id = generateNewId();
            subTask.setId(id);
            subtasks.put(id, subTask);
            epics.get(subTask.getEpicId()).addSubtaskId(subTask.getId());
            updateStatusOfEpic(subTask.getEpicId());
            return id;
        }
        return -1;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = generateNewId();
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public ArrayList<Task> getListOfTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<SubTask> getListOfSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getListOfEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Task getTaskById(int id) {
        inMemoryHistoryManager.add(tasks.get(id));
        return new Task(tasks.get(id).getTitle(), tasks.get(id).getDescription(), tasks.get(id).getId(),
                tasks.get(id).getStatus());
    }

    @Override
    public SubTask getSubTaskById(int id) {
        inMemoryHistoryManager.add(subtasks.get(id));
        return new SubTask(subtasks.get(id).getTitle(), subtasks.get(id).getDescription(), subtasks.get(id).getId(),
                subtasks.get(id).getStatus(), subtasks.get(id).getEpicId());
    }

    @Override
    public Epic getEpicById(int id) {
        inMemoryHistoryManager.add(epics.get(id));
        return new Epic(epics.get(id).getTitle(), epics.get(id).getDescription(), epics.get(id).getId(),
                epics.get(id).getStatus());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateStatusOfEpic(epic.getId());
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            epics.get(epicId).removeSubtask(id);
            updateStatusOfEpic(epicId);
            subtasks.remove(id);
        } else {
            System.out.println("Подзадача не найдена");
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> idsSubtasksOfEpic = epics.get(id).getSubtasksIds();
            for (Integer idSubtask : idsSubtasksOfEpic) {
                subtasks.remove(idSubtask);
            }
            epics.remove(id);
        } else {
            System.out.println("Эпик не найден");
        }
    }

    @Override
    public ArrayList<SubTask> getAllSubTasksOfEpic(int idEpic) {
        ArrayList<SubTask> subTasksList = new ArrayList<>();
        ArrayList<Integer> idsSubtasksOfEpic = epics.get(idEpic).getSubtasksIds();

        for (Integer idSubtask : idsSubtasksOfEpic) {
            subTasksList.add(subtasks.get(idSubtask));
        }
        return subTasksList;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.get(task.getId()) != null) {
            Task taskToUpdate = tasks.get(task.getId());
            taskToUpdate.setTitle(task.getTitle());
            taskToUpdate.setDescription(task.getDescription());
            taskToUpdate.setStatus(task.getStatus());
        } else {
            System.out.println("Задача для обновления не найдена.");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicId()) && subtasks.get(subTask.getId()) != null) {
            SubTask subTaskToUpdate = subtasks.get(subTask.getId());
            subTaskToUpdate.setTitle(subTask.getTitle());
            subTaskToUpdate.setDescription(subTask.getDescription());
            subTaskToUpdate.setStatus(subTask.getStatus());
            updateStatusOfEpic(subTaskToUpdate.getEpicId());
        } else {
            System.out.println("Подзадача для обновления не найдена.");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId()) && epics.get(epic.getId()) != null) {
            Epic epicToUpdate = epics.get(epic.getId());
            epicToUpdate.setTitle(epic.getTitle());
            epicToUpdate.setDescription(epic.getDescription());
        } else {
            System.out.println("Эпик для обновления не найден.");
        }
    }

    private void updateStatusOfEpic(int idEpic) {
        ArrayList<SubTask> subTasks = getAllSubTasksOfEpic(idEpic);
        boolean isAllNEW = true;
        boolean isAllDONE = true;

        for (SubTask subtask : subTasks) {
            if (subtask.getStatus() != Status.DONE) {
                isAllDONE = false;
            }
            if (subtask.getStatus() != Status.NEW) {
                isAllNEW = false;
            }
        }

        if (subTasks.isEmpty() || isAllNEW) {
            epics.get(idEpic).setStatus(Status.NEW);
        } else if (isAllDONE) {
            epics.get(idEpic).setStatus(Status.DONE);
        } else {
            epics.get(idEpic).setStatus(Status.IN_PROGRESS);
        }
    }

    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }
}