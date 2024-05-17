package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void tasksAddsIntoHistory() {
        TaskManager manager = Managers.getDefault();
        Task task = manager.createTask("Test");
        task.show();
        Assertions.assertTrue(manager.getHistoryManager().getHistory().contains(task));
    }

    @Test
    void oldTaskSuccessfullyDeletes() {
        TaskManager manager = Managers.getDefault();
        Task task = manager.createTask("Test");
        for (int i = 0; i < InMemoryHistoryManager.HISTORY_MEMORY + 2; i++) {
            task.show();
        }
        assertEquals(InMemoryHistoryManager.HISTORY_MEMORY - 1, manager.getHistoryManager().getHistory().size(),
                "Размер списка истории просмотров задач не совпал с его фактическим размером при переполнении");
    }
}