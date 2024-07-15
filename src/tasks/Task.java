package tasks;

import exception.TimeOverlapException;
import manager.FileBackedTaskManager;
import manager.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.*;
import java.util.Objects;

public class Task {

    protected Type type;
    private final long id;
    private String name;
    private String description;
    private Status status;
    private LocalDateTime startTime;
    private Duration duration;
    public final TaskManager taskManager;

    public Task(String name, String description, Status status, TaskManager taskManager) {
        this.name = name;
        this.description = description;
        this.taskManager = taskManager;
        this.id = taskManager.assignId();
        this.status = status;
        this.type = Type.TASK;
    }

    public Task(String name, String description, Status status, TaskManager taskManager,
                LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.taskManager = taskManager;
        this.id = taskManager.assignId();
        this.status = status;
        this.type = Type.TASK;
        setTimeBound(startTime, duration);
    }

    protected Task(long id, Type type, String name, Status status, String description,
                   TaskManager taskManager, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.status = status;
        this.description = description;
        this.taskManager = taskManager;
        taskManager.getMapOfTasks().put(id, this);
        setTimeBound(startTime, duration);
    }

    protected Task(long id, Type type, String name, Status status, String description,
                   TaskManager taskManager) {
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
        if (getStartTime() != null) {
            return String.format(String.format("%s,%s,%s,%s,%s,%s,%s", id, type, name, status, description,
                    getStartTime().toString(), getDuration().toString()));
        } else {
            return String.format(String.format("%s,%s,%s,%s,%s,,", id, type, name, status, description));
        }
    }

    public static void fromString(String value, FileBackedTaskManager taskManager) throws IOException {
        String[] data = value.trim().split(",");
        var id = data[0];
        var name = data[2];
        var status = data[3];
        String description = data[4];
        LocalDateTime startTime = LocalDateTime.parse(data[5]);
        Duration duration = Duration.parse(data[6]);

        switch (data[1]) {
            case "TASK" -> {
                Task task = new Task(Long.parseLong(id), Type.TASK, name, Status.toStatus(status),
                        description, taskManager, startTime, duration);
                taskManager.getMapOfTasks().put(task.getId(), task);
            }
            case "EPIC" -> {
                Epic epic = new Epic(Long.parseLong(id), Type.EPIC, name, Status.toStatus(status),
                        description, taskManager);
                taskManager.getMapOfTasks().put(epic.getId(), epic);
            }
            case "SUBTASK" -> {
                var epicId = data[data.length - 1];
                Subtask subtask = new Subtask(Long.parseLong(id), name, Status.toStatus(status),
                        description, Long.parseLong(epicId), taskManager, startTime, duration);
                taskManager.getMapOfTasks().put(subtask.getId(), subtask);
                Epic epic = (Epic) taskManager.getTaskById(Long.parseLong(epicId));
                epic.getMapOfSubtasks().put(subtask.getId(), subtask);
            }
            default -> {
                throw new IOException("Ошибка чтения файла! Возможно, файл поврежден.");
            }
        }
    }

    public void show() {
        taskManager.getHistoryManager().add(this);
        System.out.print(name + " [" + getStatus() + "]");
        if (!Objects.equals(getDescription(), "")) {
            System.out.print(description);
        }
        if (startTime != null) {
            DateTimeFormatter dateAndTime = DateTimeFormatter.ofPattern("dd MMMM yyг. hh:mm ");
            System.out.printf("Начало: %s| Время выполнения: %s часов|  Дедлайн: %s\n",
                    getStartTime().format(dateAndTime),
                    getDuration().toHours(),
                    getEndTime().format(dateAndTime));
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


    public void setTimeBound(LocalDateTime startTime, Duration duration) {
        this.startTime = null;
        this.duration = null;
        setStartTime(startTime);
        setDuration(duration);
        for (Task task : taskManager.getPrioritizedTasks()) {
            boolean isOverlaps = taskManager.isTimeBoundsOverlaps(startTime, duration,
                    task.getStartTime(), task.getDuration());
            if (isOverlaps && !(task.equals(this))) {
                throw new TimeOverlapException("Заданные временные рамки пересекаются" +
                        " с временными рамками другой задачи.");
            }
        }
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    private void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    private void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return getStartTime().plus(getDuration());
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