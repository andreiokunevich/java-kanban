package ru.yandex.practicum.taskmanager.http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.taskmanager.http.adapters.DurationAdapter;
import ru.yandex.practicum.taskmanager.http.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.taskmanager.manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected TaskManager taskManager;
    protected Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected void writeResponse(HttpExchange httpExchange, String responseString, int responseCode) throws IOException {
        try (OutputStream os = httpExchange.getResponseBody()) {
            httpExchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        httpExchange.close();
    }

    protected Optional<Integer> getId(HttpExchange httpExchange) {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    protected void writeTaskNotFound(HttpExchange httpExchange, int id) throws IOException {
        httpExchange.sendResponseHeaders(404, 0);
        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            outputStream.write(("Задача с id: " + id + " не найдена").getBytes(DEFAULT_CHARSET));
        }
    }

    protected void writeIntersection(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(406, 0);
        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            outputStream.write(("Задача пересекается с другими по времени.").getBytes(DEFAULT_CHARSET));
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}