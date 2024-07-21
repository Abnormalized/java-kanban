package manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Managers {

    private Managers() {

    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static TaskManager getFileManager(File file) {
        TaskManager manager = null;
        try {
            if (!Files.exists(file.toPath()) || Files.readString(file.toPath()).isEmpty()) {
                System.out.println("Файл с задачами не найден. Создаю...");
                manager = new FileBackedTaskManager(getDefaultHistory(), file);
            } else {
                System.out.println("Найден файл с задачами. Загружаю...");
                manager = FileBackedTaskManager.loadFromFile(file);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return manager;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
