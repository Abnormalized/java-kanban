import manager.*;
import tasks.*;

import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        manualScene(manager);
    }

    private static void manualScene(TaskManager manager) {
        System.out.println("Создаем задачи...");
        manager.createTask("Task 1", "Description 1");
        manager.createTask("Task 2", "Description 2");
        Epic epic = manager.createEpic("Epic 1", "Description of epic 1");
        epic.addSubtask("Subtask 1", "Description of subtask 1");
        epic.addSubtask("Subtask 2", "Description of subtask 2");
        epic.addSubtask("Subtask 3", "Description of subtask 3");
        manager.createEpic("Epic 2", "Description of epic 2");
        printAllTasks(manager);
        showHistory(manager);

        Random random = new Random();

        System.out.println("Просматриваем 3 случайные задачи");
        for (int i = 0; i < 3; i++) {
            long randomNum = random.nextLong(manager.getNextFreeId());
            Task task = manager.getMapOfTasks().get(randomNum);
            task.show();
            showHistory(manager);
        }

        System.out.println("---------------");
        System.out.println("Тест на удаление эпика с подзадачами:");
        epic.show();
        System.out.println("Удаление: " + epic.getName());
        manager.deleteTaskById(epic.getId());
        showHistory(manager);
        System.out.println("Новый вывод: ");
        printAllTasks(manager);

        System.out.println("---------------");
        System.out.println("Тест на удаление случайно задачи:");
        int[] possibleTasks = {0, 1, 6};
        int randomId = random.nextInt(possibleTasks.length);
        System.out.println("Удаление: " + manager.getTaskById(possibleTasks[randomId]).getName());
        manager.deleteTaskById(possibleTasks[randomId]);
        showHistory(manager);
        printAllTasks(manager);
    }

    private static void testMenu(TaskManager manager){
        while (true) {
            System.out.println("1) Создать задачу");
            System.out.println("2) Посмотреть задачу по ид");
            System.out.println("3) Посмотреть историю задач");
            System.out.println("0) exit");
            Scanner scanner = new Scanner(System.in);

            String command = scanner.nextLine();

            if(Objects.equals(command, "1")) {
                System.out.println("Введите имя задачи");
                manager.createTask(scanner.nextLine());
            } else if(Objects.equals(command, "2")) {
                System.out.print("Введите id задачи: ");
                manager.getMapOfTasks().get(scanner.nextLong()).show();
            } else if(Objects.equals(command, "3")) {
                showHistory(manager);
            } else if(Objects.equals(command, "0")) {
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
