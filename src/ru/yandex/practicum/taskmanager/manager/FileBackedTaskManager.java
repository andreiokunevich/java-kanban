package ru.yandex.practicum.taskmanager.manager;

import ru.yandex.practicum.taskmanager.exceptions.ManagerLoadException;
import ru.yandex.practicum.taskmanager.exceptions.ManagerSaveException;
import ru.yandex.practicum.taskmanager.tasks.Epic;
import ru.yandex.practicum.taskmanager.tasks.Status;
import ru.yandex.practicum.taskmanager.tasks.SubTask;
import ru.yandex.practicum.taskmanager.tasks.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File fileSave;

    public FileBackedTaskManager(File file) {
        this.fileSave = file;
    }

    protected void save() {
        String head = "id,type,name,status,description,epic\n";
        try (Writer fileWriter = new FileWriter(fileSave, StandardCharsets.UTF_8)) {

            fileWriter.write(head);
            for (Task task : getListOfTasks()) {
                fileWriter.write(task.toString() + "\n");
            }

            for (Epic epic : getListOfEpics()) {
                fileWriter.write(epic.toString() + "\n");
            }

            for (SubTask subTask : getListOfSubTasks()) {
                fileWriter.write(subTask.toString() + "\n");
            }

            fileWriter.write(historyToString(inMemoryHistoryManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи в файл: " + fileSave);
        }
    }

    static String historyToString(HistoryManager manager) {
        StringBuilder history = new StringBuilder();
        List<Task> historyList = manager.getHistory();
        for (int i = 0; i < historyList.size(); i++) {
            if (i != historyList.size() - 1) {
                history.append(historyList.get(i).getId()).append(",");
            } else {
                history.append(historyList.get(i).getId());
            }
        }
        return history.toString();
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        String[] elements = value.split(",");
        for (int i = 0; i < elements.length; i++) {
            history.add(Integer.parseInt(elements[i]));
        }
        return history;
    }

    public void restoreHistory(List<Integer> history) {
        List<Task> tasks = getListOfTasks();
        List<SubTask> subTasks = getListOfSubTasks();
        List<Epic> epics = getListOfEpics();

        for (Integer id : history) {
            for (Task task : tasks) {
                if (id == task.getId()) {
                    inMemoryHistoryManager.add(task);
                }
            }

            for (SubTask subTask : subTasks) {
                if (id == subTask.getId()) {
                    inMemoryHistoryManager.add(subTask);
                }
            }

            for (Epic epic : epics) {
                if (id == epic.getId()) {
                    inMemoryHistoryManager.add(epic);
                }
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
            Status status = parseStatus(elements[3]);
            String description = elements[4];

            switch (type) {
                case "TASK":
                    task = new Task(title, description, id, status);
                    break;
                case "SUBTASK":
                    int epicId = Integer.parseInt(elements[5]);
                    task = new SubTask(title, description, id, status, epicId);
                    break;
                case "EPIC":
                    task = new Epic(title, description, id, status);
                    break;
            }
        }
        return task;
    }

    private static Status parseStatus(String value) {
        if (value.equals("NEW")) {
            return Status.NEW;
        } else if (value.equals("IN_PROGRESS")) {
            return Status.IN_PROGRESS;
        } else if (value.equals("DONE")) {
            return Status.DONE;
        } else {
            return null;
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try (Reader fileReader = new FileReader(file)) {
            BufferedReader br = new BufferedReader(fileReader);
            String line;
            String lastLine = getLastLine(file);

            while (br.ready()) {
                if (!(line = br.readLine()).equals(lastLine)) {
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
                    }
                } else {
                    fileBackedTaskManager.restoreHistory(historyFromString(line));
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Произошла ошибка при загрузке файла: " + file);
        }
        return fileBackedTaskManager;
    }

    private static String getLastLine(File file) {
        String line = null;
        String nextLine;
        try (Reader fileReader = new FileReader(file)) {
            BufferedReader br = new BufferedReader(fileReader);

            while ((nextLine = br.readLine()) != null) {
                line = nextLine;
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Произошла ошибка при загрузке файла: " + file);
        }
        return line;
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