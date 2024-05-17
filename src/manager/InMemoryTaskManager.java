package manager;

import tasks.*;

import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    HashMap<Long, Task> mapOfAllTasks = new HashMap<>();
    
    HashMap<Long, Task> tasksList = new HashMap<>();
    HashMap<Long, Epic> epicsList = new HashMap<>();
    HashMap<Long, Subtask> subtasksList = new HashMap<>();

    HistoryManager historyManager;
    private long nextFreeId = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public long assignId(){
        return nextFreeId++;
    }

    @Override
    public Task createTask(String name) {
        Task task = new Task(name, "", Status.NEW, this);
        mapOfAllTasks.put(task.getId(), task);
        tasksList.put(task.getId(), task);
        return task;
    }

    @Override
    public Task createTask(String name, String description) {
        Task task = new Task(name, description, Status.NEW, this);
        mapOfAllTasks.put(task.getId(), task);
        tasksList.put(task.getId(), task);
        return task;
    }

    @Override
    public Task createTask(String name, String description, Status status) {
        Task task = new Task(name, description, Status.NEW, this);
        mapOfAllTasks.put(task.getId(), task);
        tasksList.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(String name) {
        Epic epic = new Epic(name, "", Status.NEW, this);
        mapOfAllTasks.put(epic.getId(), epic);
        epicsList.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Epic createEpic(String name, String description) {
        Epic epic = new Epic(name, description, Status.NEW, this);
        mapOfAllTasks.put(epic.getId(), epic);
        epicsList.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Epic createEpic(String name, String description, Status status) {
        Epic epic = new Epic(name, description, status, this);
        mapOfAllTasks.put(epic.getId(), epic);
        epicsList.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public HashMap<Long, Task> getMapOfTasks() {
        return mapOfAllTasks;
    }

    @Override
    public void eraseMapOfTasks() {
        mapOfAllTasks.clear();
    }

    @Override
    public Task getTaskById(long id) {
        if (mapOfAllTasks.containsKey(id)) {
            return mapOfAllTasks.get(id);
        } else {
            System.out.println("не удалось найти задачу с таким ID.");
            return null;
        }
    }

    @Override
    public Epic getEpicById(long id) {
        if (epicsList.containsKey(id)) {
            return epicsList.get(id);
        } else {
            System.out.println("не удалось найти задачу с таким ID.");
            return null;
        }
    }

    @Override
    public void deleteTaskById(long id) {
        if (mapOfAllTasks.containsKey(id)) {
            mapOfAllTasks.remove(id);
        } else {
            System.out.println("не удалось найти задачу с таким ID.");
        }
    }

    @Override
    public HashMap<Long, Task> getTasksList() {
        return tasksList;
    }

    @Override
    public HashMap<Long, Epic> getEpicsList() {
        return epicsList;
    }

    @Override
    public HashMap<Long, Subtask> getSubtasksList() {
        return subtasksList;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}
