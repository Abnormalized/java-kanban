package server;

import com.sun.net.httpserver.*;
import java.util.*;
import java.io.*;

import tasks.*;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    void getTasks(HttpExchange exchange) throws IOException {
        sendText(exchange, taskToJson(taskManager.getTaskList()));
    }

    @Override
    void getParticularTask(HttpExchange exchange, long id) throws IOException {
        Task task = taskManager.getTaskById(id);
        if (taskManager.getTaskList().contains(task)) {
            sendText(exchange, taskToJson(task));
            return;
        }
        sendNotFound(exchange, "Не удалось найти задачу с типом task и таким ID");
    }

    @Override
    void createNewTaskRequest(HttpExchange exchange) throws IOException {
        System.out.println("Получен запрос на создание задачи");
        InputStream requestBody = exchange.getRequestBody();
        Optional<Task> taskOptional = parseTask(requestBody);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            taskManager.getMapOfTasks().put(task.getId(), task);
            taskManager.getTaskList().add(task);
            exchange.sendResponseHeaders(201, 0);
            exchange.close();
        } else {
            sendBadRequest(exchange);
        }
    }

    @Override
    void updateExistTaskRequest(HttpExchange exchange, Long id) throws IOException {
        System.out.println("Получен запрос редактирование существующей задачи");
        InputStream requestBody = exchange.getRequestBody();
        Optional<Task> taskOptional = parseTask(requestBody);
        if (taskOptional.isPresent()) {
            Task newTask = taskOptional.get();
            taskManager.updateTask(taskManager.getTaskById(id), newTask);
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
        if (target instanceof Subtask) {
            sendBadRequest(exchange);
        } else if (target instanceof Epic) {
            sendBadRequest(exchange);
        } else {
            taskManager.deleteTaskById(id);
            sendText(exchange, "Подзадача с ID:" + id + " была удалена");
        }
    }
}