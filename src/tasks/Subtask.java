package tasks;

import manager.TaskManager;

public class Subtask extends Task{

    final long epicId;

    public Subtask(String name, String description, Status status, TaskManager taskManager, long epicId) {
        super(name, description, status, taskManager);
        this.epicId = epicId;
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
        try {
            taskManager.getEpicsList().get(epicId).updateStatus();
        } catch (NullPointerException e) {

        }
    }

}
