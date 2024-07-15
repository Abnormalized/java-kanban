package manager;

import org.junit.jupiter.api.*;
import java.io.*;
import java.time.*;

import tasks.*;

class FileBackedTaskManagerTest extends TaskManagerTest<TaskManager> {

    TaskManager manager;
    File file;

    @BeforeEach
    void creatingManager() {
        try {
            this.file = File.createTempFile("tmpData", "csv");
            this.manager = Managers.getFileManager(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void managerIsNotNull() {
        Assertions.assertNotNull(manager);
    }

    @Test
    void tasksWithTheSameIdsIsEquals() {
        Task createdTask = manager.createTask("test",
                LocalDateTime.of(2024, 1, 1, 10, 00), Duration.ofHours(1));
        long idOfTheTask = createdTask.getId();
        Task findedTask = manager.getTaskById(idOfTheTask);
        Assertions.assertEquals(createdTask, findedTask,
                "Объекты с одинаковым id не равны друг другу");
    }

    @Test
    void epicsWithTheSameIdsIsEquals() {
        Epic createdEpic = manager.createEpic("test");
        long idOfTheEpic = createdEpic.getId();
        Task findedEpic = manager.getTaskById(idOfTheEpic);
        Assertions.assertEquals(createdEpic, findedEpic,
                "Объекты с одинаковым id не равны друг другу");
    }

    @Test
    void subtasksWithTheSameIdsIsEquals() {
        Epic createdEpic = manager.createEpic("test");
        Subtask createdSubtask = createdEpic.addSubtask("Subtask test",
                LocalDateTime.of(2024, 1, 1, 10, 00), Duration.ofHours(1));
        long idOfTheSubtask = createdSubtask.getId();
        Task findedSubtask = manager.getTaskById(idOfTheSubtask);
        Assertions.assertEquals(createdSubtask, findedSubtask,
                "Объекты с одинаковым id не равны друг другу");
    }

    @Test
    void mapOfTasksEraseSuccessfully() {
        Epic createdEpic = manager.createEpic("test");
        createdEpic.addSubtask("Subtask test",
                LocalDateTime.of(2024, 1, 1, 10, 00), Duration.ofHours(1));
        manager.createTask("test",
                LocalDateTime.of(2024, 1, 1, 11, 00), Duration.ofHours(1));
        Assertions.assertNotEquals(manager.getMapOfTasks().size(), 0,
                "mapOfTasks остается пустным при добавлении новых задач");
        manager.clear();
        Assertions.assertEquals(manager.getMapOfTasks().size(), 0,
                "mapOfTasks не обнулился");
    }

    @Test
    void taskDeletedById() {
        Epic createdEpic = manager.createEpic("test epic");
        createdEpic.addSubtask("Subtask test",
                LocalDateTime.of(2024, 1, 1, 10, 00), Duration.ofHours(1));
        Task task = manager.createTask("test task",
                LocalDateTime.of(2024, 1, 1, 11, 00), Duration.ofHours(1));
        long taskId = task.getId();
        manager.deleteTaskById(taskId);
        Assertions.assertNull(manager.getTaskById(taskId),
                "Удалось найти задачу, которая должна была быть удалена");
    }

    @Test
    void epicsSubtaskDeleting() {
        Epic epic = manager.createEpic("test epic");
        Subtask testedSubtask = epic.addSubtask("Subtask test 1",
                LocalDateTime.of(2024, 1, 1, 10, 00), Duration.ofHours(1));
        epic.addSubtask("Subtask test 2",
                LocalDateTime.of(2024, 1, 1, 11, 00), Duration.ofHours(1));
        Long targetId = testedSubtask.getId();
        manager.deleteTaskById(targetId);
        Assertions.assertFalse(epic.getMapOfSubtasks().containsKey(targetId),
                "Внутри эпика остаются записи неактуальных id подзадач, которые были уже удалены");
    }

    @Test
    void loadingFromEmptyFile() throws IOException {
        File emptyfile = File.createTempFile("emptyData", "csv");
        FileBackedTaskManager managerFromEmptyFile = FileBackedTaskManager.loadFromFile(emptyfile);
    }

    @Test
    void saveAndLoadFeature() {
        File file = null;
        try {
            file = File.createTempFile("saveAndLoadFeatureTest", "csv");
            this.manager = Managers.getFileManager(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        manager.createTask("1 saved task",
                LocalDateTime.of(2024, 1, 1, 10, 00), Duration.ofHours(1));
        Epic epic = manager.createEpic("1 saved epic");
        manager.createSubtask(epic, "1 saved subtask",
                LocalDateTime.of(2024, 1, 1, 11, 00), Duration.ofHours(1));

        assert file != null;
        TaskManager newManager = Managers.getFileManager(file);

        Assertions.assertEquals(manager.getMapOfTasks().toString(), newManager.getMapOfTasks().toString(),
                "Ошибка сохранения/загрузки данных");
    }
}