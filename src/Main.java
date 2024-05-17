import manager.*;
import tasks.*;

public class Main {
    public static void main(String[] args) {

    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasksList().values()) {
            task.show();
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getEpicsList().values()) {
            epic.show();
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasksList().values()) {
            System.out.println(subtask.getName());
        }
        System.out.println("История:");
        for (Task task : manager.getHistoryManager().getHistory()) {
            System.out.println(task);
        }
    }
}
