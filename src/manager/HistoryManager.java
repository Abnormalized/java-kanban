package manager;

import tasks.Task;
import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(long id);

    void clear();

    List<Task> getHistory();

}
