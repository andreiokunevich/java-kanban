package ru.yandex.practicum.taskmanager.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.taskmanager.manager.TaskManager;
import ru.yandex.practicum.taskmanager.tasks.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

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
                writeResponse(httpExchange, "Неизвестный эндпоинт", 500);
        }
    }

    private void handleGetAllSubtasks(HttpExchange httpExchange) throws IOException {
        String jsonSubtask = gson.toJson(taskManager.getListOfSubTasks());
        writeResponse(httpExchange, jsonSubtask, 200);
    }

    private void handleGetSubtaskById(HttpExchange httpExchange, int id) throws IOException {
        if (taskManager.getSubTaskById(id) != null) {
            String jsonSubtask = gson.toJson(taskManager.getSubTaskById(id));
            writeResponse(httpExchange, jsonSubtask, 200);
        } else {
            writeTaskNotFound(httpExchange, id);
        }
    }

    private void handleCreateSubtask(HttpExchange httpExchange) throws IOException {
        String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (!body.isEmpty()) {
            SubTask subTask = gson.fromJson(body, SubTask.class);
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
        } else {
            writeResponse(httpExchange, "Пустое тело запроса.", 400);
        }
    }

    private void handleUpdateSubtask(HttpExchange httpExchange, int id) throws IOException {
        String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (!body.isEmpty()) {
            SubTask subTask = gson.fromJson(body, SubTask.class);
            if (taskManager.getSubTaskById(id) != null) {
                if (taskManager.checkAllIntersectionsUpdate(subTask)) {
                    taskManager.updateSubTask(subTask);
                    writeResponse(httpExchange, "Подзадача успешно обновлена.", 201);
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

    private void handleDeleteSubtask(HttpExchange httpExchange, int id) throws IOException {
        if (taskManager.getSubTaskById(id) != null) {
            taskManager.deleteSubTaskById(id);
            writeResponse(httpExchange, "Подзадача успешно удалена.", 200);
        } else {
            writeTaskNotFound(httpExchange, id);
        }
    }
}