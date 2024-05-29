package ru.yandex.practicum.taskmanager.http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.taskmanager.http.adapters.SubtaskAdapter;
import ru.yandex.practicum.taskmanager.http.adapters.TaskAdapter;
import ru.yandex.practicum.taskmanager.manager.TaskManager;
import ru.yandex.practicum.taskmanager.tasks.SubTask;
import ru.yandex.practicum.taskmanager.tasks.Task;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected Gson gsonPrioritized = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapter(Task.class, new TaskAdapter())
            .registerTypeAdapter(SubTask.class, new SubtaskAdapter())
            .create();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if (method.equals("GET")) {
            if (taskManager.getPrioritizedTasks().size() != 0) {
                String prioritizedJson = gsonPrioritized.toJson(taskManager.getPrioritizedTasks());
                writeResponse(httpExchange, prioritizedJson, 200);
            } else {
                writeResponse(httpExchange, "Список приоритетных задач на данный момент пуст.", 404);
            }
        } else {
            writeResponse(httpExchange, "Неизвестный эндпоинт", 404);
        }
    }
}