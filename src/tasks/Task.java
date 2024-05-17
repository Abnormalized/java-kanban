package tasks;

import manager.TaskManager;

import java.util.Objects;

public class Task {

    private final long id;
    private String name;
    private String description;
    private Status status;

    public final TaskManager taskManager;

    public Task(String name, String description, Status status, TaskManager taskManager) {
        this.name = name;
        this.description = description;
        this.taskManager = taskManager;
        this.id = taskManager.assignId();
        this.setStatus(status);
    }

    @Override
    public String toString() {
        return "Tasks.Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public void show() {
        taskManager.getHistoryManager().add(this);
        System.out.println(name + " [" + getStatus() + "]");
        if (!Objects.equals(getDescription(), "")) {
            System.out.println(description);
        }
    }

    public long getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
