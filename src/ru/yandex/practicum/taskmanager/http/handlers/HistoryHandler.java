package ru.yandex.practicum.taskmanager.http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.taskmanager.http.adapters.EpicAdapter;
import ru.yandex.practicum.taskmanager.http.adapters.SubtaskAdapter;
import ru.yandex.practicum.taskmanager.http.adapters.TaskAdapter;
import ru.yandex.practicum.taskmanager.manager.TaskManager;
import ru.yandex.practicum.taskmanager.tasks.Epic;
import ru.yandex.practicum.taskmanager.tasks.SubTask;
import ru.yandex.practicum.taskmanager.tasks.Task;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected Gson gsonHistory = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapter(Task.class, new TaskAdapter())
            .registerTypeAdapter(SubTask.class, new SubtaskAdapter())
            .registerTypeAdapter(Epic.class, new EpicAdapter())
            .create();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if (method.equals("GET")) {
            if (taskManager.getHistory().size() != 0) {
                String historyJson = gsonHistory.toJson(taskManager.getHistory());
                writeResponse(httpExchange, historyJson, 200);
            } else {
                writeResponse(httpExchange,"История на данный момент пуста.", 404);
            }
        } else {
            writeResponse(httpExchange, "Неизвестный эндпоинт", 404);
        }
    }
}