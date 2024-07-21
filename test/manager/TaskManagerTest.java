package manager;

import static org.junit.jupiter.api.Assertions.*;
import exception.TimeOverlapException;
import org.junit.jupiter.api.*;
import java.time.*;
import java.io.*;

import tasks.*;

abstract class TaskManagerTest<T extends TaskManager> {

    TaskManager manager;

    @BeforeEach
    void creatingManager() {
        this.manager = Managers.getDefault();
    }

    @BeforeEach
    void creatingBackedManager() {
        try {
            File file = File.createTempFile("tmpData", "csv");
            this.manager = Managers.getFileManager(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void statusOfEpicWhenAllSubsAreNew() {
        Epic epic = manager.createEpic("testEpic");
        epic.addSubtask(manager,"test1Subtask", "desc1", LocalDateTime.now(), Duration.ofHours(1));
        epic.addSubtask(manager,"test2Subtask", "desc2", LocalDateTime.now().plus(Duration.ofHours(2)),
                Duration.ofHours(1));
        assertEquals(epic.getStatus(), Status.NEW);
    }

    @Test
    void statusOfEpicWhenAllSubsAreDone() {
        Epic epic = manager.createEpic("testEpic");
        Subtask sub1 = epic.addSubtask(manager,"test1Subtask", "desc1",
                LocalDateTime.now(), Duration.ofHours(1));
        Subtask sub2 = epic.addSubtask(manager,"test2Subtask", "desc2",
                LocalDateTime.now().plus(Duration.ofHours(2)), Duration.ofHours(1));
        sub1.setStatus(manager, Status.DONE);
        sub2.setStatus(manager, Status.DONE);
        assertEquals(epic.getStatus(), Status.DONE);
    }

    @Test
    void statusOfEpicWhenSomeSubsIsNewAndSomeIsDone() {
        Epic epic = manager.createEpic("testEpic");
        Subtask sub1 = epic.addSubtask(manager, "test1Subtask", "desc1",
                LocalDateTime.now(), Duration.ofHours(1));
        epic.addSubtask(manager, "test2Subtask", "desc2",
                LocalDateTime.now().plus(Duration.ofHours(2)), Duration.ofHours(1));
        sub1.setStatus(manager, Status.DONE);
        assertEquals(epic.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    void statusOfEpicWhenAllSubsAreInProgress() {
        Epic epic = manager.createEpic("testEpic");
        Subtask sub1 = epic.addSubtask(manager, "test1Subtask", "desc1",
                LocalDateTime.now(), Duration.ofHours(1));
        Subtask sub2 = epic.addSubtask(manager, "test2Subtask", "desc2",
                LocalDateTime.now().plus(Duration.ofHours(2)), Duration.ofHours(1));
        sub1.setStatus(manager, Status.IN_PROGRESS);
        sub2.setStatus(manager, Status.IN_PROGRESS);
        assertEquals(epic.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    void subtaskReturnHisEpic() {
        Epic epic = manager.createEpic("testEpic");
        Subtask sub1 = epic.addSubtask(manager, "test1Subtask", "desc1",
                LocalDateTime.now(), Duration.ofHours(1));
        Assertions.assertEquals(manager.getTaskById(sub1.getEpicId()), epic);
    }

    @Test
    void SchedulesDoNotOverlap() {
        Task task1 = manager.createTask("test",
                LocalDateTime.of(2024, 1, 1, 10, 0), Duration.ofHours(1));
        Task task2 = manager.createTask("test",
                LocalDateTime.of(2024, 1, 1, 13, 0), Duration.ofHours(1));
        boolean result = manager.isTimeBoundsOverlaps(task1.getStartTime(), task1.getDuration(),
                task2.getStartTime(), task2.getDuration());
        Assertions.assertFalse(result, "Метод проверяющий пересечения интервалов временных рамок" +
                "отработал некорректно. Вернул true (есть пересечение), когда пересечения фактически нет.");
    }

    @Test
    public void SchedulesDoesOverlap() {
        assertThrows(TimeOverlapException.class, () -> {
            manager.createTask("test",
                    LocalDateTime.of(2024, 1, 1, 10, 0), Duration.ofHours(2));
            manager.createTask("test",
                    LocalDateTime.of(2024, 1, 1, 11, 0), Duration.ofHours(2));
        }, "Пересечение временных рамок задач не приводит к исключению.");

        assertThrows(TimeOverlapException.class, () -> {
            manager.createTask("test",
                    LocalDateTime.of(2024, 1, 1, 10, 0), Duration.ofHours(2));
            manager.createTask("test",
                    LocalDateTime.of(2024, 1, 1, 11, 0), Duration.ofHours(2));
        }, "Пересечение временных рамок задач не приводит к исключению.");

        assertThrows(TimeOverlapException.class, () -> {
            Epic epic = manager.createEpic("Epic");
            manager.createSubtask(epic, "test",
                    LocalDateTime.of(2024, 1, 1, 10, 0), Duration.ofHours(2));
            manager.createSubtask(epic, "test",
                    LocalDateTime.of(2024, 1, 1, 11, 0), Duration.ofHours(2));
        }, "Пересечение временных рамок подзадач не приводит к исключению.");

        assertThrows(TimeOverlapException.class, () -> {
            Epic epic = manager.createEpic("Epic");
            manager.createSubtask(epic, "test",
                    LocalDateTime.of(2024, 1, 1, 10, 0), Duration.ofHours(2));
            manager.createSubtask(epic, "test",
                    LocalDateTime.of(2024, 1, 1, 11, 0), Duration.ofHours(2));
        }, "Пересечение временных рамок подзадач не приводит к исключению.");

        assertThrows(TimeOverlapException.class, () -> {
            Epic epic = manager.createEpic("Epic");
            manager.createSubtask(epic, "test",
                    LocalDateTime.of(2024, 1, 1, 10, 0), Duration.ofHours(2));
            manager.createTask("test",
                    LocalDateTime.of(2024, 1, 1, 11, 0), Duration.ofHours(2));
        }, "Пересечение временных рамок задачи и подзадачи не приводит к исключению.");

        assertThrows(TimeOverlapException.class, () -> {
            Epic epic = manager.createEpic("Epic");
            manager.createTask("test",
                    LocalDateTime.of(2024, 1, 1, 10, 0), Duration.ofHours(2));
            manager.createSubtask(epic, "test",
                    LocalDateTime.of(2024, 1, 1, 11, 0), Duration.ofHours(2));
        }, "Пересечение временных рамок задачи и подзадачи не приводит к исключению.");
    }
}