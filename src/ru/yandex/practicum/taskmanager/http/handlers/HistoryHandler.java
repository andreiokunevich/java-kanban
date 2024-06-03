package ru.yandex.practicum.taskmanager.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.taskmanager.manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if (method.equals("GET")) {
            String historyJson = gson.toJson(taskManager.getHistory());
            writeResponse(httpExchange, historyJson, 200);
        } else {
            writeResponse(httpExchange, "Неизвестный эндпоинт", 500);
        }
    }
}