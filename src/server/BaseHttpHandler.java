package server;

import java.nio.charset.StandardCharsets;
import exception.TimeOverlapException;
import com.google.gson.stream.*;
import com.sun.net.httpserver.*;
import java.lang.reflect.Type;
import java.io.InputStream;

import manager.TaskManager;
import com.google.gson.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;

import tasks.*;

public abstract class BaseHttpHandler implements HttpHandler {

    static TaskManager taskManager;

    public BaseHttpHandler() {
        taskManager = HttpTaskServer.getTaskManager();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        switch (requestMethod) {
            case "GET":
                handleGetRequest(exchange);
                break;
            case "POST":
                try {
                    handlePostRequest(exchange);
                } catch (TimeOverlapException exception) {
                    sendHasInteractions(exchange);
                } catch (NoSuchElementException exception) {
                    sendNotFound(exchange, "404: Не найдено");
                }
                break;
            case "DELETE":
                try {
                    handleDeleteRequest(exchange);
                } catch (NoSuchElementException exception) {
                    sendNotFound(exchange, "404: Не найдено");
                }
                break;
            default:
                System.out.println("Был вызван неизвестный метод");
        }
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text;charset=utf-8");
        exchange.sendResponseHeaders(404, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        String text = "Создаваемая задача пересекается по времени с существующими";
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text;charset=utf-8");
        exchange.sendResponseHeaders(406, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendBadRequest(HttpExchange exchange) throws IOException {
        String text = "Некорректный запрос";
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text;charset=utf-8");
        exchange.sendResponseHeaders(400, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected String taskToJson(Task task) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Task.class, new TaskSerializer())
                .create();
        return gson.toJson(task);
    }

    protected String taskToJson(Collection<Task> list) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Task.class, new TaskSerializer())
                .create();
        return gson.toJson(list);
    }

    protected String epicToJson(Epic epic) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Epic.class, new EpicSerializer())
                .create();
        return gson.toJson(epic);
    }

    protected String epicToJson(Collection<Epic> list) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Epic.class, new EpicSerializer())
                .create();
        return gson.toJson(list);
    }

    protected String subtaskToJson(Subtask subtask) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
                .create();
        return gson.toJson(subtask);
    }

    protected String subtaskToJson(Collection<Subtask> list) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
                .create();
        return gson.toJson(list);
    }

    void handleGetRequest(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        boolean isParticular = pathParts.length == 3 || pathParts.length == 4 && pathParts[1].equals("epics");
        if (isParticular) {
            long id = Long.parseLong(pathParts[2]);
            try {
                getParticularTask(exchange, id);
            } catch (NoSuchElementException exception) {
                sendNotFound(exchange, "404: не найдено");
            }
            return;
        } else if (pathParts.length == 2) {
            getTasks(exchange);
            return;
        }
        sendBadRequest(exchange);
    }

    abstract void getTasks(HttpExchange exchange) throws IOException;

    abstract void getParticularTask(HttpExchange exchange, long id) throws IOException;

    void handlePostRequest(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        boolean isParticular = (pathParts.length == 3);
        if (isParticular) {
            updateExistTaskRequest(exchange, Long.parseLong(pathParts[2]));
            return;
        } else if (pathParts.length == 2) {
            createNewTaskRequest(exchange);
            return;
        }
        sendBadRequest(exchange);
    }

    abstract void createNewTaskRequest(HttpExchange exchange) throws IOException;

    abstract void updateExistTaskRequest(HttpExchange exchange, Long id) throws IOException;

    abstract void handleDeleteRequest(HttpExchange exchange) throws IOException;

    protected Optional<Task> parseTask(InputStream inputStream) throws IOException {
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        boolean gotRequiredData = body.contains("name") & body.contains("startTime") &
                body.contains("durationInHours");
        if (gotRequiredData) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Status.class, new StatusAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .registerTypeAdapter(Task.class, new TaskDeserializer())
                    .create();
            Task task = gson.fromJson(body, Task.class);
            return Optional.of(task);
        } else {
            return Optional.empty();
        }
    }

    protected Optional<Epic> parseEpic(InputStream inputStream) throws IOException {
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        boolean gotRequiredData = body.contains("name");
        if (gotRequiredData) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Status.class, new StatusAdapter())
                    .registerTypeAdapter(Epic.class, new EpicDeserializer())
                    .create();
            Epic epic = gson.fromJson(body, Epic.class);
            return Optional.of(epic);
        } else {
            return Optional.empty();
        }
    }

    protected Optional<Subtask> parseSubtask(InputStream inputStream) throws IOException {
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        boolean gotRequiredData = body.contains("name") & body.contains("startTime") &
                body.contains("durationInHours") & body.contains("epicId");
        if (gotRequiredData) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Status.class, new StatusAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .registerTypeAdapter(Subtask.class, new SubtaskDeserializer())
                    .create();
            Subtask subtask = gson.fromJson(body, Subtask.class);
            return Optional.of(subtask);
        } else {
            return Optional.empty();
        }
    }


    static class LocalDateAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MMMM.yyyy");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDate) throws IOException {
            if (localDate == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(localDate.format(DATE_TIME_FORMATTER));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            String dateString = jsonReader.nextString();
            if (dateString.isBlank()) {
                return null;
            }
            return LocalDateTime.parse(dateString, DATE_TIME_FORMATTER);
        }
    }

    static class DurationAdapter extends TypeAdapter<Duration> {

        @Override
        public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
            if (duration == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(duration.toHours());
        }

        @Override
        public Duration read(final JsonReader jsonReader) throws IOException {
            String durationString = jsonReader.nextString();
            if (durationString.isBlank()) {
                return null;
            }
            return Duration.ofHours(Integer.parseInt(durationString));
        }
    }

    static class StatusAdapter extends TypeAdapter<Status> {

        @Override
        public void write(JsonWriter jsonWriter, Status status) throws IOException {
            if (status == null) {
                jsonWriter.value("NEW");
                return;
            }
            jsonWriter.value(status.toString());
        }

        @Override
        public Status read(JsonReader jsonReader) throws IOException {
            String statusString = jsonReader.nextString();
            if (statusString.isBlank()) {
                return Status.NEW;
            }
            return Status.toStatus(statusString);
        }
    }

    static class TaskSerializer implements JsonSerializer<Task> {

        @Override
        public JsonElement serialize(Task task, Type type, JsonSerializationContext context) {
            JsonObject response = new JsonObject();
            String typeName = type.getTypeName().split("\\.")[1];
            response.addProperty("id", task.getId());
            response.addProperty("type", typeName);
            response.addProperty("name", task.getName());
            response.addProperty("description", task.getDescription());
            response.add("status", context.serialize(task.getStatus()));
            response.add("startTime", context.serialize(task.getStartTime()));
            response.add("durationInHours", context.serialize(task.getDuration()));
            return response;
        }
    }

    public static class TaskDeserializer implements JsonDeserializer<Task> {
        @Override
        public Task deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
                throws JsonParseException {

            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.get("description").getAsString();
            Status status = context.deserialize(jsonObject.get("status"), Status.class);
            LocalDateTime startTime = context.deserialize(jsonObject.get("startTime"), LocalDateTime.class);
            Duration duration = context.deserialize(jsonObject.get("durationInHours"), Duration.class);

            return new Task(name, description, status, taskManager, startTime, duration);
        }
    }

    static class EpicSerializer implements JsonSerializer<Epic> {

        @Override
        public JsonElement serialize(Epic epic, Type type, JsonSerializationContext context) {
            JsonObject response = new JsonObject();
            int subsCount = epic.getMapOfSubtasks().size();
            JsonElement subsList = JsonParser.parseString(Integer.toString(subsCount));
            String typeName = type.getTypeName().split("\\.")[1];

            response.addProperty("id", epic.getId());
            response.addProperty("type", typeName);
            response.add("status", context.serialize(epic.getStatus()));
            response.addProperty("name", epic.getName());
            response.addProperty("description", epic.getDescription());
            response.add("startTime", context.serialize(epic.getStartTime()));
            response.add("durationInHours", context.serialize(epic.getDuration()));
            response.add("subtasksCount", subsList);
            return response;
        }

    }

    public static class EpicDeserializer implements JsonDeserializer<Epic> {
        @Override
        public Epic deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
                throws JsonParseException {

            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.get("description").getAsString();
            Status status = context.deserialize(jsonObject.get("Status"), Status.class);

            return new Epic(name, description, status, taskManager);
        }

    }

    static class SubtaskSerializer implements JsonSerializer<Subtask> {

        @Override
        public JsonElement serialize(Subtask subtask, Type type, JsonSerializationContext context) {
            JsonObject response = new JsonObject();
            String typeName = type.getTypeName().split("\\.")[1];
            response.addProperty("id", subtask.getId());
            response.addProperty("type", typeName);
            response.add("status", context.serialize(subtask.getStatus()));
            response.addProperty("epicId", subtask.getEpicId());
            response.addProperty("name", subtask.getName());
            response.addProperty("description", subtask.getDescription());
            response.add("startTime", context.serialize(subtask.getStartTime()));
            response.add("durationInHours", context.serialize(subtask.getDuration()));
            return response;
        }
    }

    public static class SubtaskDeserializer implements JsonDeserializer<Subtask> {
        @Override
        public Subtask deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
                throws JsonParseException {

            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.get("description").getAsString();
            Status status = context.deserialize(jsonObject.get("status"), Status.class);
            int epicId = Integer.parseInt(jsonObject.get("epicId").getAsString());
            LocalDateTime startTime = context.deserialize(jsonObject.get("startTime"), LocalDateTime.class);
            Duration duration = context.deserialize(jsonObject.get("durationInHours"), Duration.class);
            return new Subtask(name, description, status, taskManager, epicId, startTime, duration);
        }
    }
}