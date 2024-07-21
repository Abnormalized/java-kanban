package tasks;

import com.google.gson.annotations.SerializedName;

import java.time.*;
import java.util.HashMap;

import manager.TaskManager;

public class Epic extends Task {
    @SerializedName("subtasks")
    HashMap<Long, Subtask> mapOfSubtasks;
    protected Type type;

    public Epic(String name, String description, Status status, TaskManager taskManager) {
        super(name, description, status, taskManager);
        this.type = Type.EPIC;
        mapOfSubtasks = new HashMap<>();
        setStatus(taskManager, Status.NEW);
    }

    protected Epic(long id, Type type, String name, Status status, String description, TaskManager taskManager) {
        super(id, type, name, status, description);
        mapOfSubtasks = new HashMap<>();
        setStatus(taskManager, Status.NEW);
    }

    public void updateStatus(TaskManager taskManager) {
        boolean isThereNew = false;
        boolean isThereInProgress = false;
        boolean isThereDone = false;

        if (getMapOfSubtasks() == null) {
            setStatus(taskManager, Status.NEW);
            return;
        }
        for (Subtask subtask : getMapOfSubtasks().values()) {
            Status subtaskStatus = subtask.getStatus();
            switch (subtaskStatus) {
                case NEW -> isThereNew = true;
                case IN_PROGRESS -> isThereInProgress = true;
                case DONE -> isThereDone = true;
            }
        }
        if (isThereInProgress || (isThereNew && isThereDone)) {
            setStatus(taskManager, Status.IN_PROGRESS);
            return;
        } else if (!isThereDone) {
            setStatus(taskManager, Status.NEW);
            return;
        }
        setStatus(taskManager, Status.DONE);
    }

    public Subtask addSubtask(TaskManager taskManager, String name, LocalDateTime startTime, Duration duration) {
        Subtask subtask = new Subtask(name, "", Status.NEW, taskManager, this.getId(), startTime, duration);
        taskManager.getMapOfTasks().put(subtask.getId(), subtask);
        getEndTime();
        updateStatus(taskManager);
        return subtask;
    }

    public Subtask addSubtask(TaskManager taskManager, String name, String description,
                              LocalDateTime startTime, Duration duration) {
        Subtask subtask = new Subtask(name, description, Status.NEW, taskManager, this.getId(), startTime, duration);
        taskManager.getMapOfTasks().put(subtask.getId(), subtask);
        getEndTime();
        updateStatus(taskManager);
        return subtask;
    }

    public Subtask addSubtask(TaskManager taskManager, String name, String description, Status status,
                              LocalDateTime startTime, Duration duration) {
        Subtask subtask = new Subtask(name, description, status, taskManager, this.getId(), startTime, duration);
        taskManager.getMapOfTasks().put(subtask.getId(), subtask);
        getEndTime();
        updateStatus(taskManager);
        return subtask;
    }

    public HashMap<Long, Subtask> getMapOfSubtasks() {
        return mapOfSubtasks;
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