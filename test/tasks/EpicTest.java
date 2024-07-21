package tasks;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.time.*;

import manager.*;


class EpicTest {

    TaskManager manager;

    @BeforeEach
    void creatingManager() {
        this.manager = Managers.getDefault();
    }

    @Test
    void statusOfEpicWhenAllSubsAreNew() {
        Epic epic = manager.createEpic("testEpic");
        epic.addSubtask(manager, "test1Subtask", "desc1", LocalDateTime.now(), Duration.ofHours(1));
        epic.addSubtask(manager, "test2Subtask", "desc2", LocalDateTime.now().plus(Duration.ofHours(2)),
                Duration.ofHours(1));

        assertEquals(epic.getStatus(), Status.NEW);
    }

    @Test
    void statusOfEpicWhenAllSubsAreDone() {
        Epic epic = manager.createEpic("testEpic");
        Subtask sub1 = epic.addSubtask(manager, "test1Subtask", "desc1",
                LocalDateTime.now(), Duration.ofHours(1));
        Subtask sub2 = epic.addSubtask(manager, "test2Subtask", "desc2",
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
        Subtask sub2 = epic.addSubtask(manager, "test2Subtask", "desc2",
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
}