package ru.yandex.practicum.taskmanager.manager;

import java.util.ArrayList;
import java.util.HashMap;
import ru.yandex.practicum.taskmanager.tasks.Epic;
import ru.yandex.practicum.taskmanager.tasks.Status;
import ru.yandex.practicum.taskmanager.tasks.SubTask;
import ru.yandex.practicum.taskmanager.tasks.Task;

public class TaskManager {

    private int id = 1;

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, SubTask> subtasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();

    private int generateNewId() {
        return id++;
    }

    public int createTask(Task task) {
        int id = generateNewId();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public int createSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicId())) {
            int id = generateNewId();
            subTask.setId(id);
            subtasks.put(id, subTask);
            epics.get(subTask.getEpicId()).addSubtaskId(subTask.getId());
            updateStatusOfEpic(subTask.getEpicId());
            return id;
        } else {
            System.out.println("ID эпика указан неверно");
        }
        return -1;
    }

    public int createEpic(Epic epic) {
        int id = generateNewId();
        epic.setId(id);
        epics.put(id, epic);
        return id;
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
        return new Task(tasks.get(id).getTitle(), tasks.get(id).getDescription(), tasks.get(id).getId(),
                tasks.get(id).getStatus());
    }

    public SubTask getSubTaskById(int id) {
        return new SubTask(subtasks.get(id).getTitle(), subtasks.get(id).getDescription(), subtasks.get(id).getId(),
                subtasks.get(id).getStatus(), subtasks.get(id).getEpicId());
    }

    public Epic getEpicById(int id) {
        return new Epic(epics.get(id).getTitle(), epics.get(id).getDescription(), epics.get(id).getId(),
                epics.get(id).getStatus());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateStatusOfEpic(epic.getId());
        }
        subtasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

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

    public ArrayList<SubTask> getAllSubTasksOfEpic(int idEpic) {
        ArrayList<SubTask> subTasksList = new ArrayList<>();
        ArrayList<Integer> idsSubtasksOfEpic = epics.get(idEpic).getSubtasksIds();

        for (Integer idSubtask : idsSubtasksOfEpic) {
            subTasksList.add(subtasks.get(idSubtask));
        }
        return subTasksList;
    }

    public void updateTask(Task task) {
        Task taskToUpdate = tasks.get(task.getId());
        taskToUpdate.setTitle(task.getTitle());
        taskToUpdate.setDescription(task.getDescription());
        taskToUpdate.setStatus(task.getStatus());
    }

    public void updateSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicId())) {
            SubTask subTaskToUpdate = subtasks.get(subTask.getId());
            subTaskToUpdate.setTitle(subTask.getTitle());
            subTaskToUpdate.setDescription(subTask.getDescription());
            subTaskToUpdate.setStatus(subTask.getStatus());
            updateStatusOfEpic(subTaskToUpdate.getEpicId());
        } else {
            System.out.println("Подзадача для обновления не найдена");
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic epicToUpdate = epics.get(epic.getId());
            epicToUpdate.setTitle(epic.getTitle());
            epicToUpdate.setDescription(epic.getDescription());
        } else {
            System.out.println("Эпик для обновления не найден");
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
}