package manager;

import java.util.List;

import tasks.Task;

public interface HistoryManager {

    void add(Task task);

    void remove(long id);

    void clear();

    List<Task> getHistory();
}