package manager;

import tasks.Task;
import java.util.List;


// Менеджер отвечающий исключительно за историю последних задач.


public interface HistoryManager {

    void add(Task task);

    void remove(long id);

    void clear();

    List<Task> getHistory();

}
