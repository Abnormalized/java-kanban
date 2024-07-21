package server;

import static org.junit.jupiter.api.Assertions.*;
import com.google.gson.*;
import org.junit.jupiter.api.*;

import static server.HttpTaskServer.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.*;
import java.util.List;

import tasks.*;

class SubtasksHandlerTest {

    Gson gson;

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(Status.class, new BaseHttpHandler.StatusAdapter())
                .registerTypeAdapter(LocalDateTime.class, new BaseHttpHandler.LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new BaseHttpHandler.DurationAdapter())
                .registerTypeAdapter(Subtask.class, new BaseHttpHandler.SubtaskSerializer())
                .registerTypeAdapter(Subtask.class, new BaseHttpHandler.SubtaskDeserializer())
                .create();
        start();
    }

    @AfterEach
    void tearDown() {
        taskManager.clear();
        stop();
    }

    @Test
    void subtaskIsCreating() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic("Epic");
        Subtask subtask = taskManager.createSubtask(epic, "Subtask", "Testing subtask", LocalDateTime.now(), Duration.ofMinutes(5));
        String subtaskJson = gson.toJson(subtask);
        taskManager.clear();
        Epic epicNew = taskManager.createEpic("Epic");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> tasksFromManager = taskManager.getSubtaskList();

        assertEquals(201, response.statusCode());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Subtask", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void code404IfThereIsIdNotOfEpic() throws IOException, InterruptedException {
        Task task = taskManager.createTask("Task", "Testing task", LocalDateTime.now(), Duration.ofMinutes(5));
        Epic epic = taskManager.createEpic("Epic");
        Subtask subtask = taskManager.createSubtask(epic, "Subtask", "Testing subtask",
                LocalDateTime.now().plus(Duration.ofMinutes(7)), Duration.ofMinutes(5));
        String subtaskJson = gson.toJson(subtask);
        taskManager.clear();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> tasksFromManager = taskManager.getSubtaskList();

        assertEquals(404, response.statusCode());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void subtasksShows() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic("Epic");
        Subtask subtask1 = taskManager.createSubtask(epic, "Subtask", "Testing subtask",
                LocalDateTime.now().plus(Duration.ofMinutes(1)), Duration.ofMinutes(5));
        Subtask subtask2 = taskManager.createSubtask(epic, "Subtask", "Testing subtask",
                LocalDateTime.now().plus(Duration.ofMinutes(7)), Duration.ofMinutes(5));
        String subtaskJson = gson.toJson(taskManager.getSubtaskList());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> tasksFromManager = taskManager.getSubtaskList();

        assertEquals(200, response.statusCode());
        assertEquals(subtaskJson, response.body());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void subtasksFinds() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic("Epic");
        Subtask subtask1 = taskManager.createSubtask(epic, "Subtask", "Testing subtask",
                LocalDateTime.now().plus(Duration.ofMinutes(1)), Duration.ofMinutes(5));
        Subtask subtask2 = taskManager.createSubtask(epic, "Subtask", "Testing subtask",
                LocalDateTime.now().plus(Duration.ofMinutes(7)), Duration.ofMinutes(5));
        String subtaskJson = gson.toJson(subtask1);
        String id = String.valueOf(subtask1.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(subtaskJson, response.body());
    }

    @Test
    void code404WhenInvalidId() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic("Epic");
        Subtask subtask1 = taskManager.createSubtask(epic, "Subtask", "Testing subtask",
                LocalDateTime.now().plus(Duration.ofMinutes(1)), Duration.ofMinutes(5));
        Subtask subtask2 = taskManager.createSubtask(epic, "Subtask", "Testing subtask",
                LocalDateTime.now().plus(Duration.ofMinutes(7)), Duration.ofMinutes(5));
        String id = String.valueOf(subtask2.getId() + 3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("404: не найдено", response.body());
    }

    @Test
    void taskDeleting() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic("Epic");
        Subtask subtask1 = taskManager.createSubtask(epic, "Subtask", "Testing subtask",
                LocalDateTime.now().plus(Duration.ofMinutes(1)), Duration.ofMinutes(5));
        String id = String.valueOf(subtask1.getId());
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(1, taskManager.getMapOfTasks().size());
    }
}