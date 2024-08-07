package manager;

import java.time.*;
import java.util.*;

import tasks.*;

public interface TaskManager {

    long assignId();

    long getNextFreeId();

    void updateTask(Task oldTask, Task newTask);

    void updateSubtask(Subtask oldSubtask, Subtask newSubtask);

    Task createTask(String name, LocalDateTime startTime, Duration duration);

    Task createTask(String name, String description, LocalDateTime startTime, Duration duration);

    Task createTask(String name, String description, Status status, LocalDateTime startTime, Duration duration);

    Epic createEpic(String name);

    Epic createEpic(String name, String description);

    Epic createEpic(String name, String description, Status status);

    Subtask createSubtask(Epic epicOfThisSubtask, String name, LocalDateTime startTime, Duration duration);

    Subtask createSubtask(Epic epicOfThisSubtask, String name, String description,
                          LocalDateTime startTime, Duration duration);

    Subtask createSubtask(Epic epicOfThisSubtask, String name, String description,
                          Status status, LocalDateTime startTime, Duration duration);

    HashMap<Long, Task> getMapOfTasks();

    List<Task> getTaskList();

    List<Epic> getEpicList();

    List<Subtask> getSubtaskList();

    Task getTaskById(long id);

    void deleteTaskById(long id);

    HistoryManager getHistoryManager();

    void clear();

    TreeSet<Task> getPrioritizedTasks();

    boolean isTimeBoundsOverlaps(LocalDateTime firstTaskStartDate, Duration firstTaskDuration,
                                 LocalDateTime secondTaskStartDate, Duration secondTaskDuration);
}