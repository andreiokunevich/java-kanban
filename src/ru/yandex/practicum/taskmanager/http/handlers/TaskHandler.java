package ru.yandex.practicum.taskmanager.http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.taskmanager.http.adapters.TaskAdapter;
import ru.yandex.practicum.taskmanager.manager.TaskManager;
import ru.yandex.practicum.taskmanager.tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    Gson gsonTask = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Task.class, new TaskAdapter())
            .create();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        Optional<Integer> idTask = getId(httpExchange);
        switch (method) {
            case "GET":
                if (idTask.isEmpty()) {
                    handleGetAllTasks(httpExchange);
                } else {
                    handleGetTaskById(httpExchange, idTask.get());
                }
                break;
            case "POST":
                if (idTask.isEmpty()) {
                    handleCreateTask(httpExchange);
                } else {
                    handleUpdateTask(httpExchange, idTask.get());
                }
                break;
            case "DELETE":
                if (idTask.isEmpty()) {
                    writeResponse(httpExchange, "Для удаления задачи необходимо указать ее ID.", 404);
                } else {
                    handleDeleteTask(httpExchange, idTask.get());
                }
                break;
            default:
                writeResponse(httpExchange, "Неизвестный эндпоинт", 404);
        }
    }

    private void handleGetAllTasks(HttpExchange httpExchange) throws IOException {
        if (taskManager.getListOfTasks().size() != 0) {
            String jsonTask = gsonTask.toJson(taskManager.getListOfTasks());
            writeResponse(httpExchange, jsonTask, 200);
        } else {
            writeResponse(httpExchange, "На текущий момент задач нет.", 404);
        }
    }

    private void handleGetTaskById(HttpExchange httpExchange, int id) throws IOException {
        if (taskManager.getTaskById(id) != null) {
            String jsonTask = gsonTask.toJson(taskManager.getTaskById(id));
            writeResponse(httpExchange, jsonTask, 200);
        } else {
            writeTaskNotFound(httpExchange, id);
        }
    }

    private void handleCreateTask(HttpExchange httpExchange) throws IOException {
        Task task = gsonTask.fromJson(new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), Task.class);
        if (taskManager.checkAllIntersections(task)) {
            taskManager.createTask(task);
            writeResponse(httpExchange, "Задача успешно создана.", 201);
        } else {
            writeIntersection(httpExchange);
        }
    }

    private void handleUpdateTask(HttpExchange httpExchange, int id) throws IOException {
        Task task = gsonTask.fromJson(new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), Task.class);
        if (taskManager.getTaskById(task.getId()) != null) {
            if (taskManager.checkAllIntersectionsUpdate(task)) {
                taskManager.updateTask(task);
                writeResponse(httpExchange, "Задача успешно обновлена.", 201);
            } else {
                writeIntersection(httpExchange);
            }
        } else {
            writeTaskNotFound(httpExchange, id);
        }
    }

    private void handleDeleteTask(HttpExchange httpExchange, int id) throws IOException {
        if (taskManager.getTaskById(id) != null) {
            taskManager.deleteTaskById(id);
            writeResponse(httpExchange, "Задача успешно удалена.", 200);
        } else {
            writeTaskNotFound(httpExchange, id);
        }
    }
}