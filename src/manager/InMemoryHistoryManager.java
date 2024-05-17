package manager;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    static List<Task> taskViewHistory = new LinkedList<>();
    static final int HISTORY_MEMORY = 10;

    @Override
    public void add(Task task) {
        int currentHistorySize = taskViewHistory.size();

        if (currentHistorySize < HISTORY_MEMORY - 1) {
            taskViewHistory.addLast(task);
        } else {
            taskViewHistory.removeFirst();
            taskViewHistory.addLast(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return taskViewHistory;
    }

}
