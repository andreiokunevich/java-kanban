package ru.yandex.practicum.taskmanager.http.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.taskmanager.tasks.Epic;
import ru.yandex.practicum.taskmanager.tasks.Status;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EpicAdapter extends TypeAdapter<Epic> {
    protected final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    @Override
    public void write(JsonWriter jsonWriter, Epic epic) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("title").value(epic.getTitle());
        jsonWriter.name("description").value(epic.getDescription());
        jsonWriter.name("id").value(epic.getId());
        jsonWriter.name("status").value(epic.getStatus().toString());
        jsonWriter.name("startTime").value(epic.getStartTime().format(formatter));
        jsonWriter.name("endTime").value(epic.getEndTime().format(formatter));
        jsonWriter.name("duration").value(epic.getDuration().toMinutes());
        jsonWriter.endObject();
    }

    @Override
    public Epic read(JsonReader jsonReader) throws IOException {
        String title = null;
        String description = null;
        int id = 0;
        Status status = null;
        Duration duration = null;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

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
                case "endTime":
                    endTime = LocalDateTime.parse(jsonReader.nextString(), formatter);
                    break;
                default:
                    jsonReader.skipValue();
                    break;
            }
        }
        jsonReader.endObject();

        return new Epic(title, description, id, status, startTime, endTime, duration);
    }
}