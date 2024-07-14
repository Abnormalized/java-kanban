package manager;

import tasks.*;
import java.util.HashMap;

public interface TaskManager {

    long assignId();

    long getNextFreeId();

    Task createTask(String name);

    Task createTask(String name, String description);

    Task createTask(String name, String description, Status status);

    Epic createEpic(String name);

    Epic createEpic(String name, String description);

    Epic createEpic(String name, String description, Status status);

    Subtask createSubtask(Epic epicOfThisSubtask, String name);

    Subtask createSubtask(Epic epicOfThisSubtask, String name, String description);

    Subtask createSubtask(Epic epicOfThisSubtask, String name, String description, Status status);

    HashMap<Long, Task> getMapOfTasks();

    Task getTaskById(long id);

    void deleteTaskById(long id);

    HistoryManager getHistoryManager();

    void clear();
}
