package manager;

import tasks.*;

import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    HashMap<Long, Task> mapOfAllTasks = new HashMap<>();

    HistoryManager historyManager;
    private long nextFreeId = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public long assignId() {
        return nextFreeId++;
    }

    @Override
    public long getNextFreeId() {
        return nextFreeId;
    }

    @Override
    public Task createTask(String name) {
        Task task = new Task(name, "", Status.NEW, this);
        mapOfAllTasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task createTask(String name, String description) {
        Task task = new Task(name, description, Status.NEW, this);
        mapOfAllTasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task createTask(String name, String description, Status status) {
        Task task = new Task(name, description, Status.NEW, this);
        mapOfAllTasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(String name) {
        Epic epic = new Epic(name, "", Status.NEW, this);
        mapOfAllTasks.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Epic createEpic(String name, String description) {
        Epic epic = new Epic(name, description, Status.NEW, this);
        mapOfAllTasks.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Epic createEpic(String name, String description, Status status) {
        Epic epic = new Epic(name, description, status, this);
        mapOfAllTasks.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public HashMap<Long, Task> getMapOfTasks() {
        return mapOfAllTasks;
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
    public void deleteTaskById(long id) {
        if (mapOfAllTasks.containsKey(id)) {
            Task target = mapOfAllTasks.get(id);
            if (target instanceof Epic) {
                Epic epic = ((Epic) mapOfAllTasks.get(id));
                for (Long subtaskId : epic.getMapOfSubtasks().keySet()) {
                    mapOfAllTasks.remove(subtaskId);
                    historyManager.remove(subtaskId);
                }
            } else if (target instanceof Subtask) {
                Subtask subtask = (Subtask) getMapOfTasks().get(id);
                Epic subtasksEpic = (Epic) getMapOfTasks().get(subtask.getEpicId());
                subtasksEpic.getMapOfSubtasks().remove(id);
            }
            mapOfAllTasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("не удалось найти задачу с таким ID.");
        }
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void clear() {
        mapOfAllTasks.clear();
        historyManager.clear();
    }

}
