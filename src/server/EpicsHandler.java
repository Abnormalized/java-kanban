package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasks.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    void handleGetRequest(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        boolean isParticular = pathParts.length == 3 || pathParts.length == 4 && pathParts[1].equals("epics");
        if (isParticular) {
            long id = Long.parseLong(pathParts[2]);
            if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                getEpicsSubsById(exchange, id);
            }
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

    @Override
    void getTasks(HttpExchange exchange) throws IOException {
        sendText(exchange, epicToJson(taskManager.getEpicList()));
    }

    @Override
    void getParticularTask(HttpExchange exchange, long id) throws IOException {
        Epic epic = (Epic) taskManager.getTaskById(id);
        if (taskManager.getEpicList().contains(epic)) {
            sendText(exchange, epicToJson(epic));
            return;
        }
        sendNotFound(exchange, "Не удалось найти задачу с типом epic и таким ID");
    }

    @Override
    void updateExistTaskRequest(HttpExchange exchange, Long id) throws IOException {
    }

    @Override
    void createNewTaskRequest(HttpExchange exchange) throws IOException {
        System.out.println("Получен запрос на создание эпика");
        InputStream requestBody = exchange.getRequestBody();
        Optional<Epic> epicOptional = parseEpic(requestBody);
        if (epicOptional.isPresent()) {
            Epic epic = epicOptional.get();
            taskManager.getMapOfTasks().put(epic.getId(), epic);
            taskManager.getEpicList().add(epic);
            exchange.sendResponseHeaders(201, 0);
            exchange.close();
        } else {
            sendBadRequest(exchange);
        }
    }

    @Override
    void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (pathParts.length != 3) {
            sendBadRequest(exchange);
            return;
        }
        long id = Long.parseLong(pathParts[2]);
        Task target = taskManager.getTaskById(id);
        if (target instanceof Epic) {
            try {
                taskManager.deleteTaskById(id);
                sendText(exchange, "Подзадача с ID:" + id + " была удалена");
            } catch (NoSuchElementException exception) {
                sendNotFound(exchange, "Не удалось найти задачу с таким ID");
            }
        } else {
            sendBadRequest(exchange);
        }
    }

    void getEpicsSubsById(HttpExchange exchange, long id) throws IOException {
        Epic epic = (Epic) taskManager.getTaskById(id);
        if (taskManager.getEpicList().contains(epic)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Status.class, new StatusAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
                    .create();
            sendText(exchange, gson.toJson(epic.getMapOfSubtasks().values()));
        }
        sendNotFound(exchange, "Не удалось найти задачу с типом epic и таким ID");
    }
}