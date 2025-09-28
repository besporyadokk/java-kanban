package test.service;

import com.yandex.fz4.model.Task;
import com.yandex.fz4.model.TaskStatus;
import com.yandex.fz4.service.HistoryManager;
import com.yandex.fz4.service.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void historyShouldSaveTasks() {

        Task task1 = new Task("a", "b");
        task1.setId(1);
        task1.setStatus(TaskStatus.NEW);

        historyManager.add(task1);

        Task task2 = historyManager.getHistory().getFirst();

        assertEquals(task1.getName(), task2.getName());
        assertEquals(task1.getDescription(), task2.getDescription());
        assertEquals(task1.getStatus(), task2.getStatus());

    }

    @Test
    void add() {

        Task task = new Task("a", "b");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }

    @Test
    void shouldHaveNoEqualTasks() {
        Task task = new Task("Test", "Description");
        task.setId(1);

        historyManager.add(task);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        Task task = new Task("a", "b");
        task.setId(1);
        historyManager.add(task);
        historyManager.remove(1);
        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    void testEmptyHistory() {

        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    void testRemoveFromHistoryBeginning() {

        Task task1 = new Task("Task1", "Desc");
        task1.setId(1);
        Task task2 = new Task("Task2", "Desc");
        task2.setId(2);
        Task task3 = new Task("Task3", "Desc");
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(1);
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(2, historyManager.getHistory().get(0).getId());
    }

    @Test
    void testRemoveFromHistoryMiddle() {

        Task task1 = new Task("Task1", "Desc");
        task1.setId(1);
        Task task2 = new Task("Task2", "Desc");
        task2.setId(2);
        Task task3 = new Task("Task3", "Desc");
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);
        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    void testRemoveFromHistoryEnd() {

        Task task1 = new Task("Task1", "Desc");
        task1.setId(1);
        Task task2 = new Task("Task2", "Desc");
        task2.setId(2);
        Task task3 = new Task("Task3", "Desc");
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(3);
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(2, historyManager.getHistory().get(1).getId());
    }

}