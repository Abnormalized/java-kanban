package tasks;

import manager.TaskManager;

public class Subtask extends Task {

    final long epicId;

    public Subtask(String name, String description, Status status, TaskManager taskManager, long epicId) {
        super(name, description, status, taskManager);
        this.type = Type.SUBTASK;
        this.epicId = epicId;
        this.setStatus(status);
    }

    protected Subtask(long id, Type type, String name, Status status, String description,
                      long epicId, TaskManager taskManager) {
        super(id, Type.TASK, name, status, description, taskManager);
        this.epicId = epicId;
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

    @Override
    public String toStringForSave() {
        return super.toStringForSave() + "," + epicId;
    }
}