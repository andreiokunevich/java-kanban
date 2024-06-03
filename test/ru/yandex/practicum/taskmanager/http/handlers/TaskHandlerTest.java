package ru.yandex.practicum.taskmanager.http.handlers;

import com.google.gson.*;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskHandlerTest {

    private static Gson gson;
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(manager);

    public TaskHandlerTest() throws IOException {
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
    public void testCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Task_1", "Task_1", 0, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(50));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getListOfTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task_1", tasksFromManager.get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Task_1", "Task_1", 0, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(50));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int idTask = manager.getListOfTasks().get(0).getId();

        Task task1 = new Task("Task_1_UPDATED", "Task_1", idTask, Status.NEW,
                LocalDateTime.now().plusDays(7), Duration.ofMinutes(5));
        String task1Json = gson.toJson(task1);
        URI urlUpdate = URI.create("http://localhost:8080/tasks/" + idTask);
        HttpRequest requestUpdate = HttpRequest.newBuilder()
                .uri(urlUpdate)
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .build();
        HttpResponse<String> responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseUpdate.statusCode());

        List<Task> tasksFromManager = manager.getListOfTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task_1_UPDATED", tasksFromManager.get(0).getTitle(), "Задача не обновилась");

        Task task2 = new Task("Task_1", "Task_1", 5, Status.NEW,
                LocalDateTime.now().plusDays(14), Duration.ofMinutes(5));
        String task2Json = gson.toJson(task2);
        URI urlUpdate1 = URI.create("http://localhost:8080/tasks/5");
        HttpRequest requestUpdate1 = HttpRequest.newBuilder()
                .uri(urlUpdate1)
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .build();
        HttpResponse<String> responseUpdate1 = client.send(requestUpdate1, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseUpdate1.statusCode());
    }

    @Test
    public void shouldNotCreateTaskWithIntersection() throws IOException, InterruptedException {
        Task task = new Task("Task_1", "Task_1", 0, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(50));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task("Task_1", "Task_1", 0, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(50));
        String task2Json = gson.toJson(task2);

        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task2Json)).build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }


    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Task_1", "Task_1", 0, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(50));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        int idTask = manager.getListOfTasks().get(0).getId();

        URI urlGet1 = URI.create("http://localhost:8080/tasks" + idTask);
        HttpRequest requestGet1 = HttpRequest.newBuilder().uri(urlGet1).GET().build();
        HttpResponse<String> response1 = client.send(requestGet1, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response1.statusCode());

        URI urlGet2 = URI.create("http://localhost:8080/tasks/5");
        HttpRequest requestGet2 = HttpRequest.newBuilder().uri(urlGet2).GET().build();
        HttpResponse<String> response2 = client.send(requestGet2, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response2.statusCode());
    }

    @Test
    public void getListOfAllTasks() throws IOException, InterruptedException {
        Task task = new Task("Task_1", "Task_1", 0, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(50));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        URI urlGet = URI.create("http://localhost:8080/tasks");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(urlGet).GET().build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        Task task = new Task("Task_1", "Task_1", 0, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(50));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int idTask = manager.getListOfTasks().get(0).getId();

        URI urlDelete = URI.create("http://localhost:8080/tasks/" + idTask);
        HttpRequest requestDelete = HttpRequest.newBuilder().uri(urlDelete).DELETE().build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseDelete.statusCode());

        URI urlDelete1 = URI.create("http://localhost:8080/tasks/5");
        HttpRequest requestDelete1 = HttpRequest.newBuilder().uri(urlDelete1).DELETE().build();
        HttpResponse<String> responseDelete1 = client.send(requestDelete1, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseDelete1.statusCode());
    }
}