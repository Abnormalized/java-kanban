package tasks;

import java.time.*;
import java.util.HashMap;

import manager.TaskManager;

public class Epic extends Task {

    HashMap<Long, Subtask> mapOfSubtasks;

    public Epic(String name, String description, Status status, TaskManager taskManager) {
        super(name, description, status, taskManager);
        this.type = Type.EPIC;
        mapOfSubtasks = new HashMap<>();
    }

    protected Epic(long id, Type type, String name, Status status, String description, TaskManager taskManager) {
        super(id, Type.TASK, name, status, description, taskManager);
        mapOfSubtasks = new HashMap<>();
    }

    @Override
    public void show() {
        super.show();
        if (!mapOfSubtasks.isEmpty()) {
            for (Subtask subtask : mapOfSubtasks.values()) {
                System.out.println("-->" + subtask.getName() + " [" + subtask.getStatus() + "]");
            }
        }
    }

    public void updateStatus() {
        boolean isThereNew = false;
        boolean isThereDone = false;

        if (getMapOfSubtasks() != null) {
            for (Subtask subtask : getMapOfSubtasks().values()) {
                if (subtask.getStatus() == Status.NEW) {
                    if (isThereDone) {
                        setStatus(Status.IN_PROGRESS);
                        return;
                    } else {
                        isThereNew = true;
                    }
                } else if (subtask.getStatus() == Status.DONE) {
                    if (isThereNew) {
                        setStatus(Status.IN_PROGRESS);
                        return;
                    } else {
                        isThereDone = true;
                    }
                } else {
                    setStatus(Status.IN_PROGRESS);
                    return;
                }
            }
            if (isThereNew) {
                setStatus(Status.NEW);
            } else if (isThereDone) {
                setStatus(Status.DONE);
            }
        }
    }

    public Subtask addSubtask(String name, LocalDateTime startTime, Duration duration) {
        Subtask subtask = new Subtask(name, "", Status.NEW, taskManager, this.getId(), startTime, duration);
        taskManager.getMapOfTasks().put(subtask.getId(), subtask);
        getMapOfSubtasks().put(subtask.getId(), subtask);
        return subtask;
    }

    public Subtask addSubtask(String name, String description, LocalDateTime startTime, Duration duration) {
        Subtask subtask = new Subtask(name, description, Status.NEW, taskManager, this.getId(), startTime, duration);
        taskManager.getMapOfTasks().put(subtask.getId(), subtask);
        getMapOfSubtasks().put(subtask.getId(), subtask);
        return subtask;
    }

    public Subtask addSubtask(String name, String description, Status status,
                              LocalDateTime startTime, Duration duration) {
        Subtask subtask = new Subtask(name, description, status, taskManager, this.getId(), startTime, duration);
        taskManager.getMapOfTasks().put(subtask.getId(), subtask);
        getMapOfSubtasks().put(subtask.getId(), subtask);
        return subtask;
    }

    public HashMap<Long, Subtask> getMapOfSubtasks() {
        return mapOfSubtasks;
    }

    public void setSubtaskStatus(Status status, Subtask subtask) {
        subtask.setStatus(status);
        updateStatus();
    }

    @Override
    public LocalDateTime getStartTime() {
        LocalDateTime minStartTime = null;
        for (Subtask value : mapOfSubtasks.values()) {
            if (minStartTime == null || value.getStartTime().isBefore(minStartTime)) {
                minStartTime = value.getStartTime();
            }
        }
        return minStartTime;
    }

    @Override
    public Duration getDuration() {
        Duration sum = null;
        for (Subtask value : mapOfSubtasks.values()) {
            if (sum == null) {
                sum = value.getDuration();
                continue;
            }
            sum = sum.plus(value.getDuration());
        }
        return sum;
    }

    @Override
    public LocalDateTime getEndTime() {
        return getStartTime().plus(getDuration());
    }
}