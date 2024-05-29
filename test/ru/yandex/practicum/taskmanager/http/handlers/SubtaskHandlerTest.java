package ru.yandex.practicum.taskmanager.http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.http.adapters.EpicAdapter;
import ru.yandex.practicum.taskmanager.http.adapters.SubtaskAdapter;
import ru.yandex.practicum.taskmanager.http.server.HttpTaskServer;
import ru.yandex.practicum.taskmanager.manager.InMemoryTaskManager;
import ru.yandex.practicum.taskmanager.manager.TaskManager;
import ru.yandex.practicum.taskmanager.tasks.Epic;
import ru.yandex.practicum.taskmanager.tasks.Status;
import ru.yandex.practicum.taskmanager.tasks.SubTask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskHandlerTest {
    private static Gson gson;
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(manager);

    SubtaskHandlerTest() throws IOException {
    }

    @BeforeAll
    static void createGson() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(SubTask.class, new SubtaskAdapter())
                .registerTypeAdapter(Epic.class, new EpicAdapter())
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
    public void testCreateSubtaskWithoutEpic() throws IOException, InterruptedException {
        SubTask subTask = new SubTask("subtask", "subtask", 0, Status.NEW, 0, LocalDateTime.now(), Duration.ofMinutes(15));
        String subTaskJson = gson.toJson(subTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals(0, manager.getListOfSubTasks().size(), "Подзадача создалась без эпика.");
    }

    @Test
    public void testCreateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic", 0, Status.NEW, LocalDateTime.now(), LocalDateTime.now().plusMinutes(25), Duration.ofMinutes(15));
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTask = new SubTask("subtask", "subtask", 0, Status.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(15));
        String subTaskJson = gson.toJson(subTask);

        URI url1 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response1.statusCode());
        assertEquals(1, manager.getListOfSubTasks().size(), "Подзадача не добавилась.");
    }

    @Test
    public void getListOfAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic", 0, Status.NEW, LocalDateTime.now(), LocalDateTime.now().plusMinutes(25), Duration.ofMinutes(15));
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTask = new SubTask("subtask", "subtask", 0, Status.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(15));
        String subTaskJson = gson.toJson(subTask);

        URI url1 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI urlGet = URI.create("http://localhost:8080/subtasks");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(urlGet).GET().build();
        HttpResponse<String> response2 = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode());
    }

    @Test
    public void deleteSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic", 0, Status.NEW, LocalDateTime.now(), LocalDateTime.now().plusMinutes(25), Duration.ofMinutes(15));
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTask = new SubTask("subtask", "subtask", 0, Status.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(15));
        String subTaskJson = gson.toJson(subTask);

        URI url1 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        int idSubTask = manager.getListOfSubTasks().get(0).getId();

        URI urlDelete = URI.create("http://localhost:8080/subtasks/" + idSubTask);
        HttpRequest requestDelete = HttpRequest.newBuilder().uri(urlDelete).DELETE().build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseDelete.statusCode());

        URI urlDelete1 = URI.create("http://localhost:8080/subtasks/5");
        HttpRequest requestDelete1 = HttpRequest.newBuilder().uri(urlDelete1).DELETE().build();
        HttpResponse<String> responseDelete1 = client.send(requestDelete1, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseDelete1.statusCode());
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic", 0, Status.NEW, LocalDateTime.now(), LocalDateTime.now().plusMinutes(25), Duration.ofMinutes(15));
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTask = new SubTask("subtask", "subtask", 0, Status.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(15));
        String subTaskJson = gson.toJson(subTask);

        URI url1 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        int idSubTask = manager.getListOfSubTasks().get(0).getId();

        URI urlGet1 = URI.create("http://localhost:8080/subtasks" + idSubTask);
        HttpRequest requestGet1 = HttpRequest.newBuilder().uri(urlGet1).GET().build();
        HttpResponse<String> response2 = client.send(requestGet1, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode());

        URI urlGet2 = URI.create("http://localhost:8080/subtasks/5");
        HttpRequest requestGet2 = HttpRequest.newBuilder().uri(urlGet2).GET().build();
        HttpResponse<String> response3 = client.send(requestGet2, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response3.statusCode());
    }
}