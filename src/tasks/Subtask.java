package tasks;

import manager.TaskManager;

public class Subtask extends Task{

    final long epicId;

    public Subtask(String name, String description, Status status, TaskManager taskManager, long epicId) {
        super(name, description, status, taskManager);
        this.epicId = epicId;
        this.setStatus(status);
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
        if (!((Epic) taskManager.getTaskById(epicId)).getMapOfSubtasks().isEmpty()) {
            ((Epic) taskManager.getTaskById(epicId)).updateStatus();
        }
    }

    public long getEpicId() {
        return epicId;
    }
}
