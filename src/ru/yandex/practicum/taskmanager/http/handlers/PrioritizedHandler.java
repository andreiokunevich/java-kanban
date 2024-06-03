package ru.yandex.practicum.taskmanager.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.taskmanager.manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if (method.equals("GET")) {
            String prioritizedJson = gson.toJson(taskManager.getPrioritizedTasks());
            writeResponse(httpExchange, prioritizedJson, 200);
        } else {
            writeResponse(httpExchange, "Неизвестный эндпоинт", 500);
        }
    }
}