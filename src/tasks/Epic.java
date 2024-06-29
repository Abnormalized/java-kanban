package tasks;

import java.util.HashMap;

import manager.TaskManager;

public class Epic extends Task {

    HashMap<Long, Subtask> mapOfSubtasks;

    public Epic(String name, String description, Status status, TaskManager taskManager) {
        super(name, description, status, taskManager);
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

    public Subtask addSubtask(String name) {
        Subtask subtask = new Subtask(name, "", Status.NEW, taskManager, this.getId());
        taskManager.getMapOfTasks().put(subtask.getId(), subtask);
        getMapOfSubtasks().put(subtask.getId(), subtask);
        return subtask;
    }

    public Subtask addSubtask(String name, String description) {
        Subtask subtask = new Subtask(name, description, Status.NEW, taskManager, this.getId());
        taskManager.getMapOfTasks().put(subtask.getId(), subtask);
        getMapOfSubtasks().put(subtask.getId(), subtask);
        return subtask;
    }

    public Subtask addSubtask(String name, String description, Status status) {
        Subtask subtask = new Subtask(name, description, status, taskManager, this.getId());
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

}
