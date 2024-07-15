package manager;

import tasks.*;

import java.time.*;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    HashMap<Long, Task> mapOfAllTasks = new HashMap<>();

    HistoryManager historyManager;
    protected long nextFreeId = 0;

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
    public Task createTask(String name, LocalDateTime startTime, Duration duration) {
        return createTask(name, "", Status.NEW, startTime, duration);
    }

    @Override
    public Task createTask(String name, String description, LocalDateTime startTime,
                           Duration duration) {
        return createTask(name, description, Status.NEW, startTime, duration);
    }

    @Override
    public Task createTask(String name, String description, Status status,
                           LocalDateTime startTime, Duration duration) {
        Task task = new Task(name, description, Status.NEW, this, startTime, duration);
        mapOfAllTasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(String name) {
        return createEpic(name, "", Status.NEW);
    }

    @Override
    public Epic createEpic(String name, String description) {
        return createEpic(name, description, Status.NEW);
    }

    @Override
    public Epic createEpic(String name, String description, Status status) {
        Epic epic = new Epic(name, description, status, this);
        mapOfAllTasks.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Epic epicOfThisSubtask, String name, LocalDateTime startTime,
                                 Duration duration) {
        return createSubtask(epicOfThisSubtask, name, "", Status.NEW, startTime, duration);
    }

    @Override
    public Subtask createSubtask(Epic epicOfThisSubtask, String name, String description,
                                 LocalDateTime startTime, Duration duration) {
        return createSubtask(epicOfThisSubtask, name, description, Status.NEW, startTime, duration);
    }

    @Override
    public Subtask createSubtask(Epic epicOfThisSubtask, String name, String description,
                                 Status status, LocalDateTime startTime, Duration duration) {
        return epicOfThisSubtask.addSubtask(name, description, status, startTime, duration);
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

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        final TreeSet<Task> prioritizedTasksSet = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        for (Task task : mapOfAllTasks.values()) {
            if (task.getStartTime() != null) {
                prioritizedTasksSet.add(task);
            }
        }
        return prioritizedTasksSet;
    }

    @Override
    public boolean isTimeBoundsOverlaps(LocalDateTime firstTaskStartDate, Duration firstTaskDuration,
                                        LocalDateTime secondTaskStartDate, Duration secondTaskDuration) {
        LocalDateTime firstTaskEndDate = firstTaskStartDate.plus(firstTaskDuration);
        LocalDateTime secondTaskEndDate = secondTaskStartDate.plus(secondTaskDuration);

        return ((firstTaskStartDate.isBefore(secondTaskEndDate)) && (secondTaskStartDate.isBefore(firstTaskEndDate))) ||
                (secondTaskStartDate.isBefore(firstTaskEndDate)) && (firstTaskStartDate.isBefore(secondTaskEndDate));
    }
}
