package ru.yandex.practicum.taskmanager.http.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.taskmanager.tasks.Status;
import ru.yandex.practicum.taskmanager.tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskAdapter extends TypeAdapter<Task> {
    protected final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");


    @Override
    public void write(JsonWriter jsonWriter, Task task) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("title").value(task.getTitle());
        jsonWriter.name("description").value(task.getDescription());
        jsonWriter.name("id").value(task.getId());
        jsonWriter.name("status").value(task.getStatus().toString());
        jsonWriter.name("startTime").value(task.getStartTime().format(formatter));
        jsonWriter.name("duration").value(task.getDuration().toMinutes());
        jsonWriter.endObject();
    }

    @Override
    public Task read(JsonReader jsonReader) throws IOException {
        String title = null;
        String description = null;
        int id = 0;
        Status status = null;
        Duration duration = null;
        LocalDateTime startTime = null;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String field = jsonReader.nextName();
            switch (field) {
                case "title":
                    title = jsonReader.nextString();
                    break;
                case "description":
                    description = jsonReader.nextString();
                    break;
                case "id":
                    id = jsonReader.nextInt();
                    break;
                case "status":
                    status = Status.valueOf(jsonReader.nextString());
                    break;
                case "duration":
                    duration = Duration.ofMinutes(jsonReader.nextLong());
                    break;
                case "startTime":
                    startTime = LocalDateTime.parse(jsonReader.nextString(), formatter);
                    break;
                default:
                    jsonReader.skipValue();
                    break;
            }
        }
        jsonReader.endObject();

        return new Task(title, description, id, status, startTime, duration);
    }
}