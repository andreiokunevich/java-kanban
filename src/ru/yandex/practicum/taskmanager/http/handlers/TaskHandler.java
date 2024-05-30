package ru.yandex.practicum.taskmanager.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.taskmanager.manager.TaskManager;
import ru.yandex.practicum.taskmanager.tasks.Task;

import java.io.IOException;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

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
                writeResponse(httpExchange, "Неизвестный эндпоинт", 500);
        }
    }

    private void handleGetAllTasks(HttpExchange httpExchange) throws IOException {
        String jsonTask = gson.toJson(taskManager.getListOfTasks());
        writeResponse(httpExchange, jsonTask, 200);
    }

    private void handleGetTaskById(HttpExchange httpExchange, int id) throws IOException {
        if (taskManager.getTaskById(id) != null) {
            String jsonTask = gson.toJson(taskManager.getTaskById(id));
            writeResponse(httpExchange, jsonTask, 200);
        } else {
            writeTaskNotFound(httpExchange, id);
        }
    }

    private void handleCreateTask(HttpExchange httpExchange) throws IOException {
        String body = new String(httpExchange.getRequestBody().readAllBytes(), BaseHttpHandler.DEFAULT_CHARSET);
        if (!body.isEmpty()) {
            Task task = gson.fromJson(body, Task.class);
            if (taskManager.checkAllIntersections(task)) {
                taskManager.createTask(task);
                writeResponse(httpExchange, "Задача успешно создана.", 201);
            } else {
                writeIntersection(httpExchange);
            }
        } else {
            writeResponse(httpExchange, "Пустое тело запроса.", 400);
        }
    }

    private void handleUpdateTask(HttpExchange httpExchange, int id) throws IOException {
        String body = new String(httpExchange.getRequestBody().readAllBytes(), BaseHttpHandler.DEFAULT_CHARSET);
        if (!body.isEmpty()) {
            Task task = gson.fromJson(body, Task.class);
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
        } else {
            writeResponse(httpExchange, "Пустое тело запроса.", 400);
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