package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.*;

class InMemoryTaskManagerTest {

    @Test
    void managerIsNotNull() {
        TaskManager manager = Managers.getDefault();
        Assertions.assertNotNull(manager);
    }

    @Test
    void tasksWithTheSameIdsIsEquals() {
        TaskManager manager = Managers.getDefault();
        Task createdTask = manager.createTask("test");
        long idOfTheTask = createdTask.getId();
        Task findedTask = manager.getTaskById(idOfTheTask);
        Assertions.assertEquals(createdTask, findedTask, "Объекты с одинаковым id не равны друг другу");
    }

    @Test
    void epicsWithTheSameIdsIsEquals() {
        TaskManager manager = Managers.getDefault();
        Epic createdEpic = manager.createEpic("test");
        long idOfTheEpic = createdEpic.getId();
        Task findedEpic = manager.getTaskById(idOfTheEpic);
        Assertions.assertEquals(createdEpic, findedEpic, "Объекты с одинаковым id не равны друг другу");
    }

    @Test
    void subtasksWithTheSameIdsIsEquals() {
        TaskManager manager = Managers.getDefault();
        Epic createdEpic = manager.createEpic("test");
        Subtask createdSubtask = createdEpic.addSubtask("Subtask test");
        long idOfTheSubtask = createdSubtask.getId();
        Task findedSubtask = manager.getTaskById(idOfTheSubtask);
        Assertions.assertEquals(createdSubtask, findedSubtask, "Объекты с одинаковым id не равны друг другу");
    }

    @Test
    void mapOfTasksEraseSuccessfully() {
        TaskManager manager = Managers.getDefault();
        Epic createdEpic = manager.createEpic("test");
        createdEpic.addSubtask("Subtask test");
        manager.createTask("test");
        Assertions.assertNotEquals(manager.getMapOfTasks().size(), 0,
                "mapOfTasks остается пустным при добавлении новых задач");
        manager.eraseMapOfTasks();
        Assertions.assertEquals(manager.getMapOfTasks().size(), 0,
                "mapOfTasks не обнулился");
    }

    @Test
    void taskDeletedById() {
        TaskManager manager = Managers.getDefault();
        Epic createdEpic = manager.createEpic("test epic");
        createdEpic.addSubtask("Subtask test");
        Task task = manager.createTask("test task");
        long taskId = task.getId();
        manager.deleteTaskById(taskId);
        Assertions.assertNull(manager.getTaskById(taskId),
                "Удалось найти задачу, которая должна была быть удалена");

    }
}