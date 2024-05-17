package manager;

import tasks.*;
import java.util.HashMap;

public interface TaskManager {

    long assignId();

    Task createTask(String name);

    Task createTask(String name, String description);

    Task createTask(String name, String description, Status status);

    Epic createEpic(String name);

    Epic createEpic(String name, String description);

    Epic createEpic(String name, String description, Status status);

    HashMap<Long, Task> getMapOfTasks();

    Task getTaskById(long id);

    Epic getEpicById(long id);

    void eraseMapOfTasks();

    void deleteTaskById(long id);

    HashMap<Long, Task> getTasksList();

    HashMap<Long, Epic> getEpicsList();

    HashMap<Long, Subtask> getSubtasksList();

    HistoryManager getHistoryManager();
}
