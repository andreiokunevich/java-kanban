package ru.yandex.practicum.taskmanager.http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.taskmanager.http.adapters.EpicAdapter;

import ru.yandex.practicum.taskmanager.http.adapters.SubtaskAdapter;
import ru.yandex.practicum.taskmanager.manager.TaskManager;
import ru.yandex.practicum.taskmanager.tasks.Epic;
import ru.yandex.practicum.taskmanager.tasks.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    Gson gsonEpic = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Epic.class, new EpicAdapter())
            .registerTypeAdapter(SubTask.class, new SubtaskAdapter())
            .create();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        Optional<Integer> idEpic = getId(httpExchange);
        switch (method) {
            case "GET":
                if (idEpic.isEmpty()) {
                    handleGetAllEpics(httpExchange);
                } else {
                    if (checkResponse(httpExchange)) {
                        handleGetEpicSubtasks(httpExchange, idEpic.get());
                    } else {
                        handleGetEpicById(httpExchange, idEpic.get());
                    }
                }
                break;
            case "POST":
                if (idEpic.isEmpty()) {
                    handleCreateEpic(httpExchange);
                } else {
                    handleUpdateEpic(httpExchange, idEpic.get());
                }
                break;
            case "DELETE":
                if (idEpic.isEmpty()) {
                    writeResponse(httpExchange, "Для удаления эпика необходимо указать его ID.", 404);
                } else {
                    handleDeleteEpic(httpExchange, idEpic.get());
                }
                break;
            default:
                writeResponse(httpExchange, "Неизвестный эндпоинт", 404);
        }

    }

    private void handleGetAllEpics(HttpExchange httpExchange) throws IOException {
        if (taskManager.getListOfEpics().size() != 0) {
            String jsonEpic = gsonEpic.toJson(taskManager.getListOfEpics());
            writeResponse(httpExchange, jsonEpic, 200);
        } else {
            writeResponse(httpExchange, "На текущий момент эпиков нет.", 404);
        }
    }

    private void handleGetEpicById(HttpExchange httpExchange, int id) throws IOException {
        Epic epic = taskManager.getEpicById(id);
        if (epic != null) {
            String jsonEpic = gsonEpic.toJson(epic);
            writeResponse(httpExchange, jsonEpic, 200);
        } else {
            writeTaskNotFound(httpExchange, id);
        }
    }

    private void handleCreateEpic(HttpExchange httpExchange) throws IOException {
        Epic epic = gsonEpic.fromJson(new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), Epic.class);
        taskManager.createEpic(epic);
        writeResponse(httpExchange, "Эпик успешно создан.", 201);
    }

    private void handleUpdateEpic(HttpExchange httpExchange, int id) throws IOException {
        if (taskManager.getEpicById(id) != null) {
            Epic epic = gsonEpic.fromJson(new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), Epic.class);
            taskManager.updateEpic(epic);
            writeResponse(httpExchange, "Эпик успешно обновлен.", 201);
        } else {
            writeTaskNotFound(httpExchange, id);
        }
    }

    private void handleDeleteEpic(HttpExchange httpExchange, int id) throws IOException {
        if (taskManager.getEpicById(id) != null) {
            taskManager.deleteEpicById(id);
            writeResponse(httpExchange, "Эпик успешно удален.", 200);
        } else {
            writeTaskNotFound(httpExchange, id);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange httpExchange, int id) throws IOException {
        try {
            if (taskManager.getAllSubTasksOfEpic(id).size() != 0) {
                String jsonEpic = gsonEpic.toJson(taskManager.getAllSubTasksOfEpic(id));
                writeResponse(httpExchange, jsonEpic, 200);
            } else {
                writeResponse(httpExchange, "На текущий момент у эпика нет подзадач.", 404);
            }
        } catch (Exception e) {
            writeTaskNotFound(httpExchange, id);
        }
    }

    private boolean checkResponse(HttpExchange httpExchange) {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        return pathParts[3].equals("subtasks");
    }
}