package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;

import java.util.List;

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
    void noDuplicatesInHistory() {
        TaskManager manager = Managers.getDefault();

        int numberOfTasks = 30 * 3;

        for (int i = 0; i < numberOfTasks/3; i++) {
            manager.createTask("Task " + i);
        }
        for (int i = 0; i < numberOfTasks/3; i++) {
            manager.createEpic("Epic " + i);
        }
        Epic epic = manager.createEpic("Epic for subtasks");
        for (int i = 0; i < numberOfTasks/3; i++) {
            epic.addSubtask("Sub " + i);
        }
        for (int i = 0; i < 2; i++) {
            for (Long id : manager.getMapOfTasks().keySet()) {
                manager.getMapOfTasks().get(id).show();
            }
        }
        assertEquals((numberOfTasks + 1), manager.getHistoryManager().getHistory().size(),
                "Размер списка истории просмотров задач не совпал с его фактическим размером при переполнении");
    }

    @Test
    void taskDeletesFromHistory() {
        TaskManager manager = Managers.getDefault();

        for (int i = 0; i < 3; i++) {
            manager.createTask("Task " + i);
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
            manager.createTask("Task " + i);
        }
        int[] requestOrder = {3, 2, 4 ,1 ,0};
        int[] invertedOrder = {0, 1, 4, 2, 3};

        for (int i = 0; i < requestOrder.length; i++) {
            manager.getTaskById(requestOrder[i]).show();
        }
        List<Task> historyList = manager.getHistoryManager().getHistory();
        boolean test = true;


        for (int i = 0; i < historyList.size(); i++) {
            if (historyList.get(i).getId() != invertedOrder[i]) {
                test = false;
            }
        }

        assertTrue(test, "Неправильная очередность в истории historyManager");
    }
}