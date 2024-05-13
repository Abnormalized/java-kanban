package tasks;

import java.util.HashMap;
import helper.Manager;

public class Epic extends Task{

    HashMap<Integer, Task> mapOfSubtasks;

    public Epic(String name, String description) {
        super(name, description);
        mapOfSubtasks = new HashMap<>();
    }

    public HashMap getEpicSubtasksMap() {
        return this.mapOfSubtasks;
    }

    public void updateEpicStatus() {
        boolean isThereNew = false;
        boolean isThereDone = false;

        for (Task task : mapOfSubtasks.values()) {
            if (task.getStatus() == Status.NEW) {
                if (isThereDone) {
                    this.setStatus(Status.IN_PROGRESS);
                    return;
                } else {
                    isThereNew = true;
                }
            } else if (task.getStatus() == Status.DONE) {
                if (isThereNew) {
                    this.setStatus(Status.IN_PROGRESS);
                    return;
                } else {
                    isThereDone = true;
                }
            } else {
                this.setStatus(Status.IN_PROGRESS);
                return;
            }
        }
        if (isThereNew) {
            this.setStatus(Status.NEW);
        } else if (isThereDone){
            this.setStatus(Status.DONE);
        }
    }

    public Subtask createSubtask(String name, String description) {
        Subtask subtask = new Subtask(name, description, this);
        mapOfSubtasks.put(Manager.getNextFreeId(), subtask);
        return subtask;
    }
}
