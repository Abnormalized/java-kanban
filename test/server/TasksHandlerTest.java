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

class TasksHandlerTest {

    Gson gson;

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
            .registerTypeAdapter(Status.class, new BaseHttpHandler.StatusAdapter())
            .registerTypeAdapter(LocalDateTime.class, new BaseHttpHandler.LocalDateAdapter())
            .registerTypeAdapter(Duration.class, new BaseHttpHandler.DurationAdapter())
            .registerTypeAdapter(Task.class, new BaseHttpHandler.TaskSerializer())
            .registerTypeAdapter(Task.class, new BaseHttpHandler.TaskDeserializer())
            .create();
        start();
    }

    @AfterEach
    void tearDown() {
        taskManager.clear();
        stop();
    }

    @Test
    void taskIsCreating() throws IOException, InterruptedException {
        Task task = taskManager.createTask("Test", "Testing task", LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);
        taskManager.clear();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromManager = taskManager.getTaskList();

        assertEquals(201, response.statusCode());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void tasksShows() throws IOException, InterruptedException {
        Task task = taskManager.createTask("Test", "Testing task", LocalDateTime.now(), Duration.ofMinutes(5));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromManager = taskManager.getTaskList();

        assertEquals(200, response.statusCode());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void taskFinds() throws IOException, InterruptedException {
        Task task1 = taskManager.createTask("Test1", "Testing task",
                LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask("Test2", "Testing task",
                LocalDateTime.now().plus(Duration.ofHours(1)), Duration.ofMinutes(5));
        String task1InJson = gson.toJson(task1);
        String id = String.valueOf(task1.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(task1InJson, response.body());
    }

    @Test
    void code404WhenInvalidId() throws IOException, InterruptedException {
        Task task1 = taskManager.createTask("Test1", "Testing task",
                LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask("Test2", "Testing task",
                LocalDateTime.now().plus(Duration.ofHours(1)), Duration.ofMinutes(5));
        String id = String.valueOf(task1.getId() + 3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
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
        Task task = taskManager.createTask("Test1", "Testing task",
                LocalDateTime.now(), Duration.ofMinutes(5));
        String id = String.valueOf(task.getId());
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getMapOfTasks().size());
    }
}