package ru.yandex.practicum.taskmanager.manager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import ru.yandex.practicum.taskmanager.tasks.Epic;
import ru.yandex.practicum.taskmanager.tasks.Status;
import ru.yandex.practicum.taskmanager.tasks.SubTask;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.util.Managers;

public class InMemoryTaskManager implements TaskManager {

    protected int id = 1;

    protected final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, SubTask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private final LocalDateTime defaultDateTime = LocalDateTime.MAX;

    private int generateNewId() {
        return id++;
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public int createTask(Task task) {
        int id = generateNewId();
        task.setId(id);

        if (task.getStartTime() != defaultDateTime) {
            Optional<Task> any = getPrioritizedTasks().stream()
                    .filter(t -> checkIntersection(t, task))
                    .findAny();
            if (any.isEmpty()) {
                tasks.put(id, task);
                prioritizedTasks.add(task);
            }
        } else {
            tasks.put(id, task);
        }
        return id;
    }

    @Override
    public int createSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicId())) {
            int id = generateNewId();
            subTask.setId(id);

            if (subTask.getStartTime() != defaultDateTime) {
                Optional<Task> any = getPrioritizedTasks().stream()
                        .filter(s -> checkIntersection(s, subTask))
                        .findAny();
                if (any.isEmpty()) {
                    subtasks.put(id, subTask);
                    epics.get(subTask.getEpicId()).addSubtaskId(subTask.getId());
                    prioritizedTasks.add(subTask);
                    updateEpicFields(epics.get(subTask.getEpicId()));
                }
            } else {
                subtasks.put(id, subTask);
                epics.get(subTask.getEpicId()).addSubtaskId(subTask.getId());
                updateStatusOfEpic(epics.get(subTask.getEpicId()));
            }
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
    public List<Task> getListOfTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getListOfSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getListOfEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            inMemoryHistoryManager.add(task);
            return new Task(task.getTitle(), task.getDescription(), task.getId(), task.getStatus(),
                    task.getStartTime(), task.getDuration());
        }
        return null;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        if (subtasks.containsKey(id)) {
            SubTask subTask = subtasks.get(id);
            inMemoryHistoryManager.add(subTask);
            return new SubTask(subTask.getTitle(), subTask.getDescription(), subTask.getId(),
                    subTask.getStatus(), subTask.getEpicId(), subTask.getStartTime(), subTask.getDuration());
        }
        return null;
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            inMemoryHistoryManager.add(epic);
            Epic copyEpic = new Epic(epic.getTitle(), epic.getDescription(), epic.getId(), epic.getStatus(),
                    epic.getStartTime(), epic.getEndTime(), epic.getDuration());
            epic.getSubtasksIds().forEach(copyEpic::addSubtaskId);
            return copyEpic;
        }
        return null;
    }

    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(id -> {
            inMemoryHistoryManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
        });
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        epics.values().forEach(epic -> {
            epic.cleanSubtaskIds();
            updateEpicFields(epic);
        });

        subtasks.keySet().forEach(id -> {
            inMemoryHistoryManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
        });
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(inMemoryHistoryManager::remove);
        epics.clear();

        subtasks.keySet().forEach(id -> {
            inMemoryHistoryManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
        });
        subtasks.clear();
    }

    @Override
    public void deleteTaskById(int id) {
        inMemoryHistoryManager.remove(id);
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            epics.get(epicId).removeSubtask(id);
            updateEpicFields(epics.get(epicId));
            inMemoryHistoryManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            epics.get(id).getSubtasksIds().forEach(idSub -> {
                inMemoryHistoryManager.remove(idSub);
                prioritizedTasks.remove(subtasks.get(idSub));
                subtasks.remove(idSub);
            });
            inMemoryHistoryManager.remove(id);
            epics.remove(id);
        }
    }

    @Override
    public List<SubTask> getAllSubTasksOfEpic(int idEpic) {
        return epics.get(idEpic).getSubtasksIds().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.get(task.getId()) != null) {
            if (task.getStartTime() != defaultDateTime) {
                Optional<Task> any = getPrioritizedTasks().stream()
                        .filter(t -> !t.equals(task))
                        .filter(t -> checkIntersection(t, task))
                        .findAny();
                if (any.isEmpty()) {
                    prioritizedTasks.remove(tasks.get(task.getId()));
                    tasks.put(task.getId(), task);
                    prioritizedTasks.add(task);
                }
            }
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicId()) && subtasks.get(subTask.getId()) != null) {
            if (subTask.getStartTime() != defaultDateTime) {
                Optional<Task> any = getPrioritizedTasks().stream()
                        .filter(s -> !s.equals(subTask))
                        .filter(s -> checkIntersection(s, subTask))
                        .findAny();
                if (any.isEmpty()) {
                    prioritizedTasks.remove(subtasks.get(subTask.getId()));
                    subtasks.put(subTask.getId(), subTask);
                    prioritizedTasks.add(subTask);
                    updateEpicFields(epics.get(subTask.getEpicId()));
                }
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId()) && epics.get(epic.getId()) != null) {
            Epic epicToUpdate = epics.get(epic.getId());
            epicToUpdate.setTitle(epic.getTitle());
            epicToUpdate.setDescription(epic.getDescription());
        }
    }

    private void updateStatusOfEpic(Epic epic) {
        List<SubTask> subTasks = getAllSubTasksOfEpic(epic.getId());
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
            epic.setStatus(Status.NEW);
        } else if (isAllDONE) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void updateEpicFields(Epic epic) {
        updateStatusOfEpic(epic);

        epic.setDuration(Duration.ofMinutes(getAllSubTasksOfEpic(epic.getId()).stream()
                .mapToLong(subTask -> subTask.getDuration().toMinutes())
                .sum()));

        epic.setStartTime((getAllSubTasksOfEpic(epic.getId()).stream()
                .map(SubTask::getStartTime)
                .min(LocalDateTime::compareTo)).orElse(defaultDateTime));

        epic.setEndTime((getAllSubTasksOfEpic(epic.getId()).stream()
                .map(SubTask::getEndTime)
                .filter(localDateTime -> !localDateTime.isEqual(defaultDateTime))
                .max(LocalDateTime::compareTo)).orElse(defaultDateTime));
    }

    private boolean checkIntersection(Task task1, Task task2) {
        boolean var1 = task1.getEndTime().isBefore(task2.getStartTime());
        boolean var2 = task1.getEndTime().isEqual(task2.getStartTime()) && task1.getStartTime().isBefore(task2.getEndTime());
        boolean var3 = task2.getEndTime().isBefore(task1.getStartTime());
        boolean var4 = task2.getEndTime().isEqual(task1.getStartTime()) && task2.getStartTime().isBefore(task1.getEndTime());
        return !(var1 || var2 || var3 || var4);
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }
}