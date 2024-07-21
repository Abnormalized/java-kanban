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

class EpicsHandlerTest {

    Gson gson;

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(Status.class, new BaseHttpHandler.StatusAdapter())
                .registerTypeAdapter(LocalDateTime.class, new BaseHttpHandler.LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new BaseHttpHandler.DurationAdapter())
                .registerTypeAdapter(Epic.class, new BaseHttpHandler.EpicSerializer())
                .registerTypeAdapter(Epic.class, new BaseHttpHandler.EpicDeserializer())
                .create();
        start();
    }

    @AfterEach
    void tearDown() {
        taskManager.clear();
        stop();
    }

    @Test
    void epicIsCreating() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic("Epic");
        String taskJson = gson.toJson(epic);
        taskManager.clear();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertNotNull(taskManager.getMapOfTasks(), "Задачи не возвращаются");
        assertEquals(1, taskManager.getMapOfTasks().size(), "Некорректное количество задач");
        assertEquals("Epic", taskManager.getMapOfTasks().get(0L).getName(), "Некорректное имя задачи");
    }

    @Test
    void epicsShows() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic("Epic");
        Subtask subtask = taskManager.createSubtask(epic, "test", LocalDateTime.now(), Duration.ofMinutes(2));
        String epicInJson = "[" + gson.toJson(epic) + "]";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> tasksFromManager = taskManager.getEpicList();

        assertEquals(200, response.statusCode());
        assertEquals(epicInJson, response.body());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void epicsShowsHisSubs() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic("Epic");
        taskManager.createSubtask(epic, "sub1",
                LocalDateTime.of(2024, 1, 1, 10, 00), Duration.ofMinutes(2));
        taskManager.createSubtask(epic, "sub2",
                LocalDateTime.of(2024, 1, 3, 10, 00), Duration.ofMinutes(2));

        String expectedResultInJsonFormat = "[{\"id\":1,\"type\":\"Subtask\",\"status\":\"NEW\",\"epicId\":0," +
                "\"name\":\"sub1\",\"description\":\"\",\"startTime\":\"10:00 01.января.2024\",\"durationInHours\":0}," +
                "{\"id\":2,\"type\":\"Subtask\",\"status\":\"NEW\",\"epicId\":0,\"name\":\"sub2\"," +
                "\"description\":\"\",\"startTime\":\"10:00 03.января.2024\",\"durationInHours\":0}]";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> tasksFromManager = taskManager.getEpicList();

        assertEquals(200, response.statusCode());
        assertEquals(expectedResultInJsonFormat, response.body());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void epicFinds() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic("Epic");
        taskManager.createSubtask(epic, "sub", "Testing sub",
                LocalDateTime.now().plus(Duration.ofHours(1)), Duration.ofMinutes(5));
        String epicInJson = gson.toJson(epic);
        String id = String.valueOf(epic.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(epicInJson, response.body());
    }

    @Test
    void code404WhenInvalidId() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic("Epic");
        taskManager.createSubtask(epic, "sub", "Testing sub",
                LocalDateTime.now().plus(Duration.ofHours(1)), Duration.ofMinutes(5));
        String epicInJson = gson.toJson(epic);
        String id = String.valueOf(epic.getId() + 3);

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
    void epicDeleting() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic("Epic");
        taskManager.createSubtask(epic, "sub", "Testing sub",
                LocalDateTime.now().plus(Duration.ofHours(1)), Duration.ofMinutes(5));

        String id = String.valueOf(epic.getId());
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + id);
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