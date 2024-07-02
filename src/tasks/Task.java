package tasks;

import manager.FileBackedTaskManager;
import manager.TaskManager;

import java.io.IOException;
import java.util.Objects;

public class Task {

    protected Type type;
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
        this.status = status;
        this.type = Type.TASK;
    }

    protected Task(long id, Type type, String name, Status status, String description, TaskManager taskManager) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.status = status;
        this.description = description;
        this.taskManager = taskManager;
        taskManager.getMapOfTasks().put(id, this);
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

    public String toStringForSave() {
        return id + "," + type + "," + name + "," + status + "," + description;
    }

    public static Task fromString(String value, FileBackedTaskManager taskManager) throws IOException {
        String[] data = value.trim().split(",");
        var id = data[0];
        var name = data[2];
        var status = data[3];
        String description = null;
        if (data.length == 5) {
            description = data[4];
        } else {
            description = "";
        }

        switch (data[1]) {
            case "TASK" -> {
                return new Task(Long.parseLong(id), Type.TASK, name, Status.toStatus(status),
                        description, taskManager);
            }
            case "EPIC" -> {
                return new Epic(Long.parseLong(id), Type.EPIC, name, Status.toStatus(status),
                        description, taskManager);
            }
            case "SUBTASK" -> {
                var epicId = data[5];
                return new Subtask(Long.parseLong(id), Type.EPIC, name, Status.toStatus(status),
                        description, Long.parseLong(epicId), taskManager);
            }
            default -> {
                throw new IOException("Ошибка чтения файла! Возможно, файл поврежден.");
            }
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) &&
                status == task.status && Objects.equals(taskManager, task.taskManager);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}