package manager;

import exception.TimeOverlapException;
import java.time.*;
import java.util.*;

import tasks.*;

public class InMemoryTaskManager implements TaskManager {

    HashMap<Long, Task> mapOfAllTasks = new HashMap<>();
    List<Task> taskList = new ArrayList<>();
    List<Epic> epicList = new ArrayList<>();
    List<Subtask> subtaskList = new ArrayList<>();
    HistoryManager historyManager;
    protected long nextFreeId = 0;

    public List<Task> getTaskList() {
        return taskList;
    }

    public List<Epic> getEpicList() {
        return epicList;
    }

    public List<Subtask> getSubtaskList() {
        return subtaskList;
    }

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
    public void updateTask(Task oldTask, Task newTask) {
        long id = oldTask.getId();
        newTask.setTimeBound(this, newTask.getStartTime(), newTask.getDuration());
        deleteTaskById(id);
        newTask.setId(id);
        getMapOfTasks().put(newTask.getId(), newTask);
        taskList.remove(oldTask);
        taskList.add(newTask);
    }

    @Override
    public void updateSubtask(Subtask oldSubtask, Subtask newSubtask) {
        long id = oldSubtask.getId();
        deleteTaskById(id);
        newSubtask.setId(id);
        getMapOfTasks().put(newSubtask.getId(), newSubtask);
        newSubtask.setEpicId(oldSubtask.getEpicId());
        Epic epic = (Epic) getTaskById(newSubtask.getEpicId());
        epic.getMapOfSubtasks().put(newSubtask.getId(), newSubtask);
        subtaskList.remove(newSubtask);
        subtaskList.add(newSubtask);
        epic.updateStatus(this);
    }

    @Override
    public Task createTask(String name, LocalDateTime startTime, Duration duration) {
        return createTask(name, "", Status.NEW, startTime, duration);
    }

    @Override
    public Task createTask(String name, String description, LocalDateTime startTime, Duration duration) {
        return createTask(name, description, Status.NEW, startTime, duration);
    }

    @Override
    public Task createTask(String name, String description, Status status,
                           LocalDateTime startTime, Duration duration) throws TimeOverlapException {
        Task task = new Task(name, description, Status.NEW, this, startTime, duration);
        getMapOfTasks().put(task.getId(), task);
        taskList.add(task);
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
        epicList.add(epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Epic epicOfThisSubtask, String name, LocalDateTime startTime, Duration duration) {
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
        Subtask subtask = epicOfThisSubtask.addSubtask(this, name, description, status, startTime, duration);
        subtaskList.add(subtask);
        return subtask;
    }

    @Override
    public HashMap<Long, Task> getMapOfTasks() {
        return mapOfAllTasks;
    }

    @Override
    public Task getTaskById(long id) throws NoSuchElementException {
        if (mapOfAllTasks.containsKey(id)) {
            Task task = mapOfAllTasks.get(id);
            historyManager.add(task);
            return task;
        } else {
            System.out.println("Была запрошена задача с несуществующим ID");
            throw new NoSuchElementException();
        }
    }

    @Override
    public void deleteTaskById(long id) {
        if (mapOfAllTasks.containsKey(id)) {
            Task target = mapOfAllTasks.get(id);
            if (target instanceof Epic) {
                Epic epic = ((Epic) mapOfAllTasks.get(id));
                for (Long subtaskId : epic.getMapOfSubtasks().keySet()) {
                    subtaskList.remove((Subtask) getTaskById(subtaskId));
                    mapOfAllTasks.remove(subtaskId);
                    historyManager.remove(subtaskId);
                }
                epicList.remove(epic);
            } else if (target instanceof Subtask) {
                Subtask subtask = (Subtask) getMapOfTasks().get(id);
                Epic subtasksEpic = (Epic) getMapOfTasks().get(subtask.getEpicId());
                subtasksEpic.getMapOfSubtasks().remove(id);
                subtaskList.remove(subtask);
            }
            mapOfAllTasks.remove(id);
            historyManager.remove(id);
            taskList.remove(target);
        } else {
            System.out.println("Была запрошена задача с несуществующим ID");
            throw new NoSuchElementException();
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
        nextFreeId = 0;
        epicList.clear();
        subtaskList.clear();
        taskList.clear();
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