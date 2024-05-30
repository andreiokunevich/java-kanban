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

class EpicHandlerTest {
    private static Gson gson;
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(manager);

    EpicHandlerTest() throws IOException {
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
    public void testCreateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic", 0, Status.NEW, LocalDateTime.now(), LocalDateTime.now().plusMinutes(25), Duration.ofMinutes(15));
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getListOfEpics().size(), "Эпик не создался.");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic", 0, Status.NEW, LocalDateTime.now(), LocalDateTime.now().plusMinutes(25), Duration.ofMinutes(15));
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        URI urlGet = URI.create("http://localhost:8080/epics/");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(urlGet).GET().build();
        HttpResponse<String> response2 = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode());
    }

    @Test
    public void testGetEpicsSubtasks() throws IOException, InterruptedException {
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

        int idEpic = manager.getListOfEpics().get(0).getId();

        URI urlGet = URI.create("http://localhost:8080/epics/" + idEpic + "/subtasks");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(urlGet).GET().build();
        HttpResponse<String> response2 = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode());
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic", 0, Status.NEW, LocalDateTime.now(), LocalDateTime.now().plusMinutes(25), Duration.ofMinutes(15));
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int idEpic = manager.getListOfEpics().get(0).getId();

        URI urlDelete = URI.create("http://localhost:8080/epics/" + idEpic);
        HttpRequest requestDelete = HttpRequest.newBuilder().uri(urlDelete).DELETE().build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseDelete.statusCode());
        assertEquals(0,manager.getListOfEpics().size(),"Эпик не удалился.");

        URI urlDelete1 = URI.create("http://localhost:8080/epics/5");
        HttpRequest requestDelete1 = HttpRequest.newBuilder().uri(urlDelete1).DELETE().build();
        HttpResponse<String> responseDelete1 = client.send(requestDelete1, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseDelete1.statusCode());
    }
}