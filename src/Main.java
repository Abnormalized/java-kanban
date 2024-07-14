import java.io.File;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;

import manager.*;
import tasks.*;

public class Main {
    public static void main(String[] args) {
        File file = Paths.get("data.csv").toFile();
        TaskManager manager = Managers.getFileManager(file);

        manualScene(manager);
    }

    private static void manualScene(TaskManager manager) {
        Task task1 = manager.createTask("1", LocalDateTime.now(), Duration.ofDays(2));
        Epic epic = manager.createEpic("Epic");
        manager.createSubtask(epic, "sub1", task1.getEndTime(), Duration.ofDays(1));
        manager.createSubtask(epic, "sub2", task1.getEndTime().plus(Duration.ofDays(1)), Duration.ofDays(2));
        Task task3 = manager.createTask("3", epic.getEndTime(), Duration.ofDays(2));
        Task task4 = manager.createTask("4", task3.getEndTime(), Duration.ofDays(1));

        System.out.println(manager.getPrioritizedTasks());
        printAllTasks(manager);
    }

    private static void testMenu(TaskManager manager) {
        while (true) {
            System.out.println("1) Создать задачу");
            System.out.println("2) Посмотреть задачу по ид");
            System.out.println("3) Посмотреть историю задач");
            System.out.println("0) exit");
            Scanner scanner = new Scanner(System.in);

            String command = scanner.nextLine();

            if (Objects.equals(command, "1")) {
                System.out.println("Введите имя задачи");
                manager.createTask(scanner.nextLine(), LocalDateTime.now(), Duration.ofSeconds(1));
            } else if (Objects.equals(command, "2")) {
                System.out.print("Введите id задачи: ");
                manager.getMapOfTasks().get(scanner.nextLong()).show();
            } else if (Objects.equals(command, "3")) {
                showHistory(manager);
            } else if (Objects.equals(command, "0")) {
                System.exit(0);
            }
        }
    }

    private static void showHistory(TaskManager manager) {
        System.out.println("История:");
        for (Task task : manager.getHistoryManager().getHistory()) {
            System.out.println(task);
        }
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getMapOfTasks().values()) {
            task.show();
        }
    }
}
