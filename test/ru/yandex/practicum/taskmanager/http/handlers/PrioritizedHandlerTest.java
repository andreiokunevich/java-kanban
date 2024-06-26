package ru.yandex.practicum.taskmanager.http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.http.adapters.DurationAdapter;
import ru.yandex.practicum.taskmanager.http.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.taskmanager.http.server.HttpTaskServer;
import ru.yandex.practicum.taskmanager.manager.InMemoryTaskManager;
import ru.yandex.practicum.taskmanager.manager.TaskManager;
import ru.yandex.practicum.taskmanager.tasks.Status;
import ru.yandex.practicum.taskmanager.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PrioritizedHandlerTest {
    private static Gson gson;
    protected TaskManager manager = new InMemoryTaskManager();
    protected HttpTaskServer httpTaskServer = new HttpTaskServer(manager);

    public PrioritizedHandlerTest() throws IOException {
    }

    @BeforeAll
    static void createGson() {
        gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    public void testPrioritized() throws IOException, InterruptedException {
        Task task = new Task("Task_1", "Task_1", 0, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(50));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        URI urlPrioritized = URI.create("http://localhost:8080/prioritized");
        HttpRequest request1 = HttpRequest.newBuilder().uri(urlPrioritized).GET().build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response1.statusCode());
        assertEquals(1, manager.getPrioritizedTasks().size(), "Размер списка приоритетных задач ошибочен.");
    }
}