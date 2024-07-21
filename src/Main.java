import java.time.*;

import manager.*;
import tasks.*;

public class Main {
    public static void main(String[] args) {
//        File file = Paths.get("data.csv").toFile();
//        TaskManager manager = Managers.getFileManager(file);
//
//        manualScene(manager);

        TaskManager taskManager = Managers.getDefault();
        Task task = taskManager.createTask("qwe", LocalDateTime.now(), Duration.ofHours(1));
        Epic epic = taskManager.createEpic("test");
        Subtask subtask = taskManager.createSubtask(epic, "ta", LocalDateTime.now().plus(Duration.ofHours(1)), Duration.ofHours(1));

        taskManager.getTaskById(0);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        System.out.println(taskManager.getHistoryManager().getHistory());
    }

    private static void manualScene(TaskManager manager) {
        Task task1 = manager.createTask("1", LocalDateTime.now(), Duration.ofDays(2));
        Epic epic = manager.createEpic("Epic");
        manager.createSubtask(epic, "sub1", task1.getEndTime(), Duration.ofDays(1));
        manager.createSubtask(epic, "sub2", task1.getEndTime().plus(Duration.ofDays(1)), Duration.ofDays(2));
        Task task3 = manager.createTask("3", epic.getEndTime(), Duration.ofDays(2));
        Task task4 = manager.createTask("4", task3.getEndTime(), Duration.ofDays(1));

        System.out.println(manager.getPrioritizedTasks());
    }

    private static void showHistory(TaskManager manager) {
        System.out.println("История:");
        for (Task task : manager.getHistoryManager().getHistory()) {
            System.out.println(task);
        }
    }

}
