package manager;

import exception.ManagerSaveException;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;

import tasks.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File saveFile;

    public FileBackedTaskManager(HistoryManager historyManager, File saveFile) {
        super(historyManager);
        this.saveFile = saveFile;
    }

    public void save() {
        try {
            if (!Files.exists(saveFile.toPath())) {
                throw new ManagerSaveException("Ошибка: Целевой файл записи не найден.");
            }
        } catch (IOException exception) {
            exception.getStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile, StandardCharsets.UTF_8))) {
            writer.write("Next_free_id:" + getNextFreeId() + ";");
            writer.newLine();
            writer.write("id,type,name,status,description,epicId,startTime,duration;");
            List<Task> saveQueue = new ArrayList<>(getMapOfTasks().values());
            saveQueue.sort(Comparator.comparing(Task::toString));
            for (Task task : saveQueue) {
                writer.newLine();
                writer.write(task.toStringForSave() + ";");
            }
        } catch (IOException exception) {
            System.out.println("Ошибка: Не удалось сохранить данные.");
            exception.getStackTrace();
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager =
                new FileBackedTaskManager(new InMemoryHistoryManager(), file);
        try {
            if (Files.readString(file.toPath()).isEmpty()) {
                return fileBackedTaskManager;
            }
            String[] data = Files.readString(file.toPath()).split(";");
            String[] idLine = data[0].split(":");
            long nextFreeId = Long.parseLong(idLine[1]);
            fileBackedTaskManager.setNextFreeId(nextFreeId);
            for (int i = 2; i < data.length; i++) {
                Task.fromString(data[i], fileBackedTaskManager);
            }
        } catch (IOException exception) {
            System.out.println("Ошибка: Не удалось прочитать данные из целевого файла записей.");
        }
        return fileBackedTaskManager;
    }

    @Override
    public List<Task> getTaskList() {
        save();
        return taskList;
    }

    @Override
    public List<Epic> getEpicList() {
        save();
        return epicList;
    }

    @Override
    public List<Subtask> getSubtaskList() {
        save();
        return subtaskList;
    }

    @Override
    public void updateTask(Task oldTask, Task newTask) {
        super.updateTask(oldTask, newTask);
        save();
    }

    @Override
    public void updateSubtask(Subtask oldSubtask, Subtask newSubtask) {
        super.updateSubtask(oldSubtask, newSubtask);
        save();
    }


    @Override
    public Task createTask(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        Task task = super.createTask(name, description, status, startTime, duration);
        save();
        return task;
    }

    @Override
    public Epic createEpic(String name, String description, Status status) {
        Epic epic = super.createEpic(name, description, status);
        save();
        return epic;
    }

    @Override
    public Subtask createSubtask(Epic epicOfThisSubtask, String name, String description, Status status,
                                 LocalDateTime startTime, Duration duration) {
        Subtask subtask = super.createSubtask(epicOfThisSubtask, name, description, status, startTime, duration);
        save();
        return subtask;
    }

    @Override
    public Task getTaskById(long id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public void deleteTaskById(long id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void clear() {
        super.clear();
        save();
    }

    public void setNextFreeId(long nextFreeId) {
        this.nextFreeId = nextFreeId;
    }
}