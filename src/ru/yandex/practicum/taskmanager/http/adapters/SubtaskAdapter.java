package ru.yandex.practicum.taskmanager.http.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.taskmanager.tasks.Status;
import ru.yandex.practicum.taskmanager.tasks.SubTask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubtaskAdapter extends TypeAdapter<SubTask> {
    protected final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");


    @Override
    public void write(JsonWriter jsonWriter, SubTask subTask) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("title").value(subTask.getTitle());
        jsonWriter.name("description").value(subTask.getDescription());
        jsonWriter.name("id").value(subTask.getId());
        jsonWriter.name("status").value(subTask.getStatus().toString());
        jsonWriter.name("startTime").value(subTask.getStartTime().format(formatter));
        jsonWriter.name("duration").value(subTask.getDuration().toMinutes());
        jsonWriter.name("epicId").value(subTask.getEpicId());
        jsonWriter.endObject();
    }

    @Override
    public SubTask read(JsonReader jsonReader) throws IOException {
        String title = null;
        String description = null;
        int id = 0;
        Status status = null;
        Duration duration = null;
        LocalDateTime startTime = null;
        int epicId = 0;

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
                case "epicId":
                    epicId = jsonReader.nextInt();
                    break;
                default:
                    jsonReader.skipValue();
                    break;
            }
        }
        jsonReader.endObject();

        return new SubTask(title, description, id, status, epicId, startTime, duration);
    }
}