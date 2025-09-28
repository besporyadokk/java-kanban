package test.service;

import com.yandex.fz4.model.Task;
import com.yandex.fz4.service.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File testFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            testFile = File.createTempFile("test", ".tmp");
            return new FileBackedTaskManager(testFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp file", e);
        }
    }

    @Test
    void shouldSaveAndLoadEmptyFile() {
        FileBackedTaskManager manager = createTaskManager();
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        assertTrue(loadedManager.getTasks().isEmpty());
        assertTrue(loadedManager.getEpics().isEmpty());
        assertTrue(loadedManager.getSubtasks().isEmpty());
        assertTrue(loadedManager.getHistory().isEmpty());
    }

    @Test
    void shouldSaveAndLoadTasks() {
        FileBackedTaskManager manager = createTaskManager();

        Task task1 = manager.addTask(new Task("a", "b"));
        Task task2 = manager.addTask(new Task("aa", "bb"));

        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        assertEquals(2, loadedManager.getTasks().size());
        Task loadedTask1 = loadedManager.getTaskById(task1.getId());
        Task loadedTask2 = loadedManager.getTaskById(task2.getId());

        assertEquals(task1.getName(), loadedTask1.getName());
        assertEquals(task2.getName(), loadedTask2.getName());
    }
}