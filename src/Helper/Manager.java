package Helper;

import Tasks.Epic;
import Tasks.Task;

import java.util.HashMap;

public class Manager {

    static HashMap<Integer, Task> mapOfTasks = new HashMap<>();
    private static int nextFreeId;

    public static int getNextFreeId() {
        return nextFreeId++;
    }

    public static HashMap getListOfTasks() {
            return mapOfTasks;
    }

    public static void eraseListOfTasks() {
        mapOfTasks.clear();
    }

    public static Task getTaskById(int id) {
        if (mapOfTasks.containsKey(id)) {
            return mapOfTasks.get(id);
        } else {
            System.out.println("не удалось найти задачу с таким ID.");
            return null;
        }
    }

    public static Task createTask(String name, String description) {
        Task task = new Task(name, description);
        mapOfTasks.put(task.id, task);
        return task;
    }

    public static Epic createEpic(String name, String description) {
        Epic epic = new Epic(name, description);
        mapOfTasks.put(epic.id, epic);
        return epic;
    }

    public static void deleteById(int id) {
        if (mapOfTasks.containsKey(id)) {
            mapOfTasks.remove(id);
        } else {
            System.out.println("не удалось найти задачу с таким ID.");
        }
    }



}
