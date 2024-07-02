package manager;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

import exception.ManagerSaveException;
import tasks.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File saveFile;

    public FileBackedTaskManager(HistoryManager historyManager, File saveFile) {
        super(historyManager);
        this.saveFile = saveFile;
    }

    static void main(String[] args) {
        File file = Paths.get("data2.csv").toFile();
        TaskManager manager = Managers.getFileManager(file);
        System.out.println("Создаем задачи...");
        manager.createTask("Task 1", "Description 1");
        manager.createTask("Task 2", "Description 2");
        Epic epic = manager.createEpic("Epic 1", "Description of epic 1");
        epic.addSubtask("Subtask 1", "Description of subtask 1");
        epic.addSubtask("Subtask 2", "Description of subtask 2");
        epic.addSubtask("Subtask 3", "Description of subtask 3");
        manager.createEpic("Epic 2", "Description of epic 2");
        System.out.println("Задачи первого менеджера:");
        for (Task task : manager.getMapOfTasks().values()) {
            task.show();
        }
        System.out.println("Создаем новый FileBackedTaskManager...");
        TaskManager newManager = Managers.getFileManager(file);
        System.out.println("Задачи нового менеджера:");
        for (Task task : newManager.getMapOfTasks().values()) {
            task.show();
        }
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
            writer.write("id,type,name,status,description,epicId;");
            for (Long id : getMapOfTasks().keySet()) {
                writer.newLine();
                writer.write(getMapOfTasks().get(id).toStringForSave() + ";");
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
    public Task createTask(String name, String description, Status status) {
        Task task = super.createTask(name, description, status);
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
    public Subtask createSubtask(Epic epicOfThisSubtask, String name, String description, Status status) {
        Subtask subtask = super.createSubtask(epicOfThisSubtask, name, description, status);
        save();
        return subtask;
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

