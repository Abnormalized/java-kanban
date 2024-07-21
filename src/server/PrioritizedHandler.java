package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equals("GET")) {
            getTasks(exchange);
        } else {
            System.out.println("Был вызван неизвестный метод");
        }
    }

    @Override
    void getTasks(HttpExchange exchange) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Task.class, new TaskSerializer())
                .registerTypeAdapter(Epic.class, new EpicSerializer())
                .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
                .create();
        sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()));
    }

    @Override
    void getParticularTask(HttpExchange exchange, long id) throws IOException {

    }

    @Override
    void updateExistTaskRequest(HttpExchange exchange, Long id) throws IOException {

    }

    @Override
    void createNewTaskRequest(HttpExchange exchange) throws IOException {

    }

    @Override
    void handleDeleteRequest(HttpExchange exchange) {

    }
}