package server;

import com.sun.net.httpserver.*;
import java.io.*;
import java.util.*;

import tasks.*;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    void getTasks(HttpExchange exchange) throws IOException {
        sendText(exchange, subtaskToJson(taskManager.getSubtaskList()));
    }

    @Override
    void getParticularTask(HttpExchange exchange, long id) throws IOException {
        Subtask subtask = (Subtask) taskManager.getTaskById(id);
        if (taskManager.getSubtaskList().contains(subtask)) {
            sendText(exchange, subtaskToJson(subtask));
            return;
        }
        sendNotFound(exchange, "Не удалось найти задачу с типом subtask и таким ID");
    }

    @Override
    void createNewTaskRequest(HttpExchange exchange) throws IOException, NoSuchElementException {
        System.out.println("Получен запрос на создание подзадачи");
        InputStream requestBody = exchange.getRequestBody();
        Optional<Subtask> taskOptional = parseSubtask(requestBody);
        if (taskOptional.isPresent()) {
            Subtask subtask = taskOptional.get();
            Epic epic = (Epic) taskManager.getTaskById(subtask.getEpicId());
            taskManager.getMapOfTasks().put(subtask.getId(), subtask);
            taskManager.getSubtaskList().add(subtask);
            epic.getMapOfSubtasks().put(subtask.getId(), subtask);
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
        Optional<Subtask> subtaskOptional = parseSubtask(requestBody);
        if (subtaskOptional.isPresent()) {
            Subtask newSubtask = subtaskOptional.get();
            taskManager.updateSubtask((Subtask) taskManager.getTaskById(id), newSubtask);
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
}