package server;

import com.sun.net.httpserver.*;
import java.io.IOException;
import com.google.gson.*;
import java.time.*;

import tasks.*;

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
        sendBadRequest(exchange);
    }

    @Override
    void updateExistTaskRequest(HttpExchange exchange, Long id) throws IOException {
        sendBadRequest(exchange);
    }

    @Override
    void createNewTaskRequest(HttpExchange exchange) throws IOException {
        sendBadRequest(exchange);
    }

    @Override
    void handleDeleteRequest(HttpExchange exchange) throws IOException {
        sendBadRequest(exchange);
    }
}