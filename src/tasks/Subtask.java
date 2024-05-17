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
        if (!taskManager.getEpicsList().get(epicId).getMapOfSubtasks().isEmpty()) {
            taskManager.getEpicsList().get(epicId).updateStatus();
        } else {
            System.out.println("У этого эпика нет подзадач");
        }
    }

}
