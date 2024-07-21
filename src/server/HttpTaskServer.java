package server;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

public class HttpTaskServer {
    private static final int PORT = 8080;
    static HttpServer server;
    static TaskManager taskManager = Managers.getFileManager(Paths.get("data.csv").toFile());

    public static void main(String[] args) throws InterruptedException {
        start();
    }

    public static void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException exception) {
            System.out.println("Не удалось создать сервер");
        }
        server.createContext("/tasks", new TasksHandler());
        server.createContext("/subtasks", new SubtasksHandler());
        server.createContext("/epics", new EpicsHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/prioritized", new PrioritizedHandler());
        server.start();
        System.out.println("Сервер начал работу. Порт: " + PORT);
    }

    public static void stop() {
        server.stop(0);
    }

    public static TaskManager getTaskManager() {
        return taskManager;
    }
}
