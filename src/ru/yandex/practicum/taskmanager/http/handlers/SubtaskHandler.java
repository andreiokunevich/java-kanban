package ru.yandex.practicum.taskmanager.http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.taskmanager.http.adapters.SubtaskAdapter;
import ru.yandex.practicum.taskmanager.manager.TaskManager;
import ru.yandex.practicum.taskmanager.tasks.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    Gson gsonSubTask = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(SubTask.class, new SubtaskAdapter())
            .create();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        Optional<Integer> idSubtask = getId(httpExchange);
        switch (method) {
            case "GET":
                if (idSubtask.isEmpty()) {
                    handleGetAllSubtasks(httpExchange);
                } else {
                    handleGetSubtaskById(httpExchange, idSubtask.get());
                }
                break;
            case "POST":
                if (idSubtask.isEmpty()) {
                    handleCreateSubtask(httpExchange);
                } else {
                    handleUpdateSubtask(httpExchange, idSubtask.get());
                }
                break;
            case "DELETE":
                if (idSubtask.isEmpty()) {
                    writeResponse(httpExchange, "Для удаления подзадачи необходимо указать ее ID.", 404);
                } else {
                    handleDeleteSubtask(httpExchange, idSubtask.get());
                }
                break;
            default:
                writeResponse(httpExchange, "Неизвестный эндпоинт", 404);
        }
    }

    private void handleGetAllSubtasks(HttpExchange httpExchange) throws IOException {
        if (taskManager.getListOfSubTasks().size() != 0) {
            String jsonSubtask = gsonSubTask.toJson(taskManager.getListOfSubTasks());
            writeResponse(httpExchange, jsonSubtask, 200);
        } else {
            writeResponse(httpExchange, "На текущий момент подзадач нет.", 404);
        }
    }

    private void handleGetSubtaskById(HttpExchange httpExchange, int id) throws IOException {
        if (taskManager.getSubTaskById(id) != null) {
            String jsonSubtask = gsonSubTask.toJson(taskManager.getSubTaskById(id));
            writeResponse(httpExchange, jsonSubtask, 200);
        } else {
            writeTaskNotFound(httpExchange, id);
        }
    }

    private void handleCreateSubtask(HttpExchange httpExchange) throws IOException {
        SubTask subTask = gsonSubTask.fromJson(new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), SubTask.class);
        if (taskManager.checkAllIntersections(subTask)) {
            int subtaskId = taskManager.createSubTask(subTask);
            if (subtaskId == -1) {
                writeResponse(httpExchange, "Подзадача не может быть создана без эпика.", 404);
            } else {
                writeResponse(httpExchange, "Подзадача успешно создана.", 201);
            }
        } else {
            writeIntersection(httpExchange);
        }
    }

    private void handleUpdateSubtask(HttpExchange httpExchange, int id) throws IOException {
        if (taskManager.getSubTaskById(id) != null) {
            SubTask subTask = gsonSubTask.fromJson(new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), SubTask.class);
            if (taskManager.checkAllIntersectionsUpdate(subTask)) {
                taskManager.updateSubTask(subTask);
                writeResponse(httpExchange, "Подзадача успешно обновлена.", 201);
            } else {
                writeIntersection(httpExchange);
            }
        } else {
            writeTaskNotFound(httpExchange, id);
        }
    }

    private void handleDeleteSubtask(HttpExchange httpExchange, int id) throws IOException {
        if (taskManager.getSubTaskById(id) != null) {
            taskManager.deleteSubTaskById(id);
            writeResponse(httpExchange, "Подзадача успешно удалена.", 200);
        } else {
            writeTaskNotFound(httpExchange, id);
        }
    }
}