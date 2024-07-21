package manager;

import org.junit.jupiter.api.*;
import java.util.List;
import java.time.*;

import tasks.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void tasksAddsIntoHistory() {
        TaskManager manager = Managers.getDefault();
        Task task = manager.createTask("Test",
                LocalDateTime.of(2024, 1, 1, 10, 0), Duration.ofHours(1));
        manager.getTaskById(task.getId());
        Assertions.assertTrue(manager.getHistoryManager().getHistory().contains(task));
    }

    @Test
    void noDuplicatesInHistory() {
        TaskManager manager = Managers.getDefault();
        int numberOfTasks = 30 * 3;
        for (int i = 0; i < numberOfTasks / 3; i++) {
            manager.createTask("Task " + i,
                    LocalDateTime.of(2024 + i, 1, 1, 1, 00), Duration.ofHours(10));
        }
        for (int i = 0; i < numberOfTasks / 3; i++) {
            manager.createEpic("Epic " + i);
        }
        Epic epic = manager.createEpic("Epic for subtasks");
        for (int i = 0; i < numberOfTasks / 3; i++) {
            epic.addSubtask(manager, "Sub " + i,
                    LocalDateTime.of(2024 + i, 3, 1, 1, 00), Duration.ofHours(10));
        }
        for (int i = 0; i < 2; i++) {
            for (Long id : manager.getMapOfTasks().keySet()) {
                manager.getTaskById(id);

            }
        }
        assertEquals((numberOfTasks + 1), manager.getHistoryManager().getHistory().size(),
                "Размер списка истории просмотров задач не совпал с его фактическим размером при переполнении");
    }

    @Test
    void taskDeletesFromHistory() {
        TaskManager manager = Managers.getDefault();
        for (int i = 0; i < 3; i++) {
            manager.createTask("Task " + i,
                    LocalDateTime.of(2024, 1, 1, 10 + i, 00), Duration.ofHours(1));
        }
        int targetId = 0;
        manager.deleteTaskById(targetId);
        boolean test = true;
        for (Task task : manager.getHistoryManager().getHistory()) {
            if (task.getId() == targetId) {
                test = false;
            }
        }
        assertTrue(test, "Задачи не удаляются из истории просмотров после их полного удаления");
    }

    @Test
    void historyArrayInRightSubsequence() {
        TaskManager manager = Managers.getDefault();
        for (int i = 0; i < 5; i++) {
            manager.createTask("Task " + i,
                    LocalDateTime.of(2024, 1, 1, 10 + i, 00), Duration.ofHours(1));
        }
        int[] requestOrder = {3, 2, 4, 1, 0};
        int[] invertedOrder = {0, 1, 4, 2, 3};
        for (int i = 0; i < requestOrder.length; i++) {
            manager.getTaskById(manager.getTaskById(requestOrder[i]).getId());
        }
        List<Task> historyList = manager.getHistoryManager().getHistory();
        boolean test = true;
        for (int i = 0; i < historyList.size(); i++) {
            if (historyList.get(i).getId() != invertedOrder[i]) {
                test = false;
            }
        }
        assertTrue(test, "метод getHistory класса HistoryManager возвращает задачи не в порядке " +
                "последовательности запросов к ним.");
    }

    @Test
    void historyManagerClears() {
        TaskManager manager = Managers.getDefault();
        for (int i = 0; i < 5; i++) {
            manager.createTask("Task " + i,
                    LocalDateTime.of(2024, 1, 1, 10 + i, 0), Duration.ofHours(1));
        }
        int[] requestOrder = {3, 2, 4, 1, 0};
        for (int i = 0; i < requestOrder.length; i++) {
            manager.getTaskById(manager.getTaskById(requestOrder[i]).getId());
        }
        manager.getHistoryManager().clear();
        assertEquals(0, manager.getHistoryManager().getHistory().size());
    }
}