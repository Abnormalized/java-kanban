package tasks;

import java.time.*;
import java.util.ArrayList;
import exception.TimeOverlapException;

import manager.TaskManager;

public class Subtask extends Task {

    long epicId;
    protected Type type;

    public Subtask(String name, String description, Status status,
                   TaskManager taskManager, long epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, status, taskManager, startTime, duration);
        this.type = Type.SUBTASK;
        this.epicId = epicId;
        addIntoEpicsCollection((Epic) taskManager.getTaskById(epicId));
        this.setStatus(taskManager, status);
    }

    protected Subtask(long id, String name, Status status, String description,
                      long epicId, TaskManager taskManager, LocalDateTime startTime, Duration duration) {
        super(id, Type.SUBTASK, name, status, description);
        this.epicId = epicId;
        addIntoEpicsCollection((Epic) taskManager.getTaskById(epicId));
        setTimeBound(taskManager, startTime, duration);
        this.setStatus(taskManager, status);
    }

    @Override
    public void setStatus(TaskManager taskManager, Status status) {
        super.setStatus(taskManager, status);
        Epic epic = (Epic) taskManager.getTaskById(epicId);
        if (!(epic).getMapOfSubtasks().isEmpty()) {
            ((Epic) taskManager.getTaskById(epicId)).updateStatus(taskManager);
        }
    }

    private void addIntoEpicsCollection(Epic epic) {
        if (!epic.getMapOfSubtasks().containsValue(this)) {
            epic.getMapOfSubtasks().put(getId(), this);
        }
    }

    public long getEpicId() {
        return epicId;
    }

    public void setEpicId(long id) {
        this.epicId = id;
    }

    @Override
    public String toStringForSave() {
        return super.toStringForSave() + "," + epicId;
    }

    @Override
    public void setTimeBound(TaskManager taskManager, LocalDateTime startTime,
                             Duration duration) throws TimeOverlapException {
        this.startTime = null;
        this.duration = null;
        setStartTime(startTime);
        setDuration(duration);
        ArrayList<Task> listOfTasksWithDate = new ArrayList<>(taskManager.getPrioritizedTasks());
        for (Task task : listOfTasksWithDate) {
            if (task instanceof Epic epic) {
                for (Subtask subtask : epic.getMapOfSubtasks().values()) {
                    boolean isOverlaps = taskManager.isTimeBoundsOverlaps(startTime, duration,
                            subtask.getStartTime(), subtask.getDuration());
                    if ((isOverlaps) && !(subtask.equals(this) && (epicId != epic.getId()))) {
                        throw new TimeOverlapException("Заданные временные рамки пересекаются" +
                                " с временными рамками другой задачи.");
                    }
                }
            } else {
                boolean isOverlaps = taskManager.isTimeBoundsOverlaps(startTime, duration,
                        task.getStartTime(), task.getDuration());
                if (isOverlaps && !(task.equals(this))) {
                    throw new TimeOverlapException("Заданные временные рамки пересекаются" +
                            " с временными рамками другой задачи.");
                }
            }
        }
    }
}