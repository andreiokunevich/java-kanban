package ru.yandex.practicum.taskmanager.http.server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.taskmanager.http.handlers.*;
import ru.yandex.practicum.taskmanager.manager.TaskManager;
import ru.yandex.practicum.taskmanager.util.Managers;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;

    protected TaskManager taskManager;
    protected TaskHandler taskHandler;
    protected SubtaskHandler subtaskHandler;
    protected EpicHandler epicHandler;
    protected HistoryHandler historyHandler;
    protected PrioritizedHandler prioritizedHandler;
    protected HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.taskHandler = new TaskHandler(taskManager);
        this.subtaskHandler = new SubtaskHandler(taskManager);
        this.epicHandler = new EpicHandler(taskManager);
        this.historyHandler = new HistoryHandler(taskManager);
        this.prioritizedHandler = new PrioritizedHandler(taskManager);
    }

    public void start() {
        httpServer.createContext("/tasks", taskHandler);
        httpServer.createContext("/subtasks", subtaskHandler);
        httpServer.createContext("/epics", epicHandler);
        httpServer.createContext("/history", historyHandler);
        httpServer.createContext("/prioritized", prioritizedHandler);
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту.");
    }

    public void stop() {
        httpServer.stop(1);
        System.out.println("HTTP-сервер остановлен.");
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getFileBackedTaskManager(new File("data.csv")));
        httpTaskServer.start();
        httpTaskServer.stop();
    }
}