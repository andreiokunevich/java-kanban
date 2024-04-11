package ru.yandex.practicum.taskmanager.manager;

import ru.yandex.practicum.taskmanager.exceptions.ManagerLoadException;
import ru.yandex.practicum.taskmanager.exceptions.ManagerSaveException;
import ru.yandex.practicum.taskmanager.tasks.Epic;
import ru.yandex.practicum.taskmanager.tasks.Status;
import ru.yandex.practicum.taskmanager.tasks.SubTask;
import ru.yandex.practicum.taskmanager.tasks.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File fileSave;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public FileBackedTaskManager(File file) {
        this.fileSave = file;
    }

    protected void save() {
        String head = "id,type,name,status,description,startTime,endTime,duration,epic\n";
        try (Writer fileWriter = new FileWriter(fileSave, StandardCharsets.UTF_8)) {
            fileWriter.write(head);
            Stream.concat(Stream.concat(getListOfTasks().stream(), getListOfEpics().stream()), getListOfSubTasks().stream())
                    .map(this::toString)
                    .map(s -> s + "\n")
                    .forEach(s -> {
                        try {
                            fileWriter.write(s);
                        } catch (IOException e) {
                            throw new ManagerSaveException("Произошла ошибка во время записи в файл: " + fileSave);
                        }
                    });
            fileWriter.write(" \n");
            fileWriter.write(historyToString(inMemoryHistoryManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи в файл: " + fileSave);
        }
    }

    public String toString(Task task) {
        String taskInString;
        if (task instanceof SubTask) {
            taskInString = task.getId() + "," + task.getType() + "," + task.getTitle() + ","
                    + task.getStatus() + "," + task.getDescription() + ","
                    + task.getStartTime().format(DATE_TIME_FORMATTER) + ","
                    + task.getEndTime().format(DATE_TIME_FORMATTER) + "," + task.getDuration().toMinutes() + "," + ((SubTask) task).getEpicId();
        } else {
            taskInString = task.getId() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + task.getStartTime().format(DATE_TIME_FORMATTER) + ","
                    + task.getEndTime().format(DATE_TIME_FORMATTER) + "," + task.getDuration().toMinutes();
        }
        return taskInString;
    }

    static String historyToString(HistoryManager manager) {
        return manager.getHistory().stream()
                .map(Task::getId)
                .map(i -> i + ",")
                .collect(Collectors.joining());
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if (value != null) {
            String[] elements = value.split(",");
            if (Character.isDigit(value.charAt(0))) {
                for (String element : elements) {
                    history.add(Integer.parseInt(element));
                }
            }
        }
        return history;
    }

    public void restoreHistory(List<Integer> history) {
        for (Integer id : history) {
            if (tasks.get(id) != null) {
                inMemoryHistoryManager.add(tasks.get(id));
            } else if (subtasks.get(id) != null) {
                inMemoryHistoryManager.add(subtasks.get(id));
            } else if (epics.get(id) != null) {
                inMemoryHistoryManager.add(epics.get(id));
            } else {
                return;
            }
        }
    }

    public static Task fromString(String value) {
        Task task = null;
        if (Character.isDigit(value.charAt(0))) {
            String[] elements = value.split(",");
            int id = Integer.parseInt(elements[0]);
            String type = elements[1];
            String title = elements[2];
            String status = elements[3];
            String description = elements[4];
            LocalDateTime startTime = LocalDateTime.parse(elements[5], DATE_TIME_FORMATTER);
            LocalDateTime endTime = LocalDateTime.parse(elements[6], DATE_TIME_FORMATTER);
            Duration duration = Duration.ofMinutes(Integer.parseInt(elements[7]));

            switch (type) {
                case "TASK":
                    task = new Task(title, description, id, Status.valueOf(status), startTime, duration);
                    break;
                case "SUBTASK":
                    int epicId = Integer.parseInt(elements[8]);
                    task = new SubTask(title, description, id, Status.valueOf(status), epicId, startTime, duration);
                    break;
                case "EPIC":
                    task = new Epic(title, description, id, Status.valueOf(status), startTime, endTime, duration);
                    break;
            }
        }
        return task;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try (Reader fileReader = new FileReader(file)) {
            BufferedReader br = new BufferedReader(fileReader);
            String line;
            int maxId = 0;

            br.readLine();
            while (br.ready()) {
                if (!(line = br.readLine()).equals(" ")) {
                    Task task = fromString(line);
                    if (task != null) {
                        if (task instanceof Epic) {
                            fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                        } else if (task instanceof SubTask) {
                            fileBackedTaskManager.subtasks.put(task.getId(), (SubTask) task);
                            int epicId = ((SubTask) task).getEpicId();
                            fileBackedTaskManager.epics.get(epicId).addSubtaskId(task.getId());
                        } else {
                            fileBackedTaskManager.tasks.put(task.getId(), task);
                        }

                        if (task.getId() > maxId) {
                            maxId = task.getId();
                        }
                    }
                } else {
                    fileBackedTaskManager.restoreHistory(historyFromString(br.readLine()));
                }
            }
            fileBackedTaskManager.id = maxId + 1;
        } catch (IOException e) {
            throw new ManagerLoadException("Произошла ошибка при загрузке файла: " + file);
        }
        return fileBackedTaskManager;
    }

    @Override
    public int createTask(Task task) {
        int idTask = super.createTask(task);
        save();
        return idTask;
    }

    @Override
    public int createSubTask(SubTask subTask) {
        int idSubtask = super.createSubTask(subTask);
        save();
        return idSubtask;
    }

    @Override
    public int createEpic(Epic epic) {
        int idEpic = super.createEpic(epic);
        save();
        return idEpic;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }
}