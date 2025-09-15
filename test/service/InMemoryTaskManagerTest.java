package test.service;


import com.yandex.fz4.service.InMemoryTaskManager;
import com.yandex.fz4.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.yandex.fz4.model.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void differentMadeIdsDoNotConflict() {
        Task task1 = new Task("a", "b");
        manager.addTask(task1);
        Task task2 = new Task("b", "a");
        task2.setId(3);
        manager.addTask(task2);
        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void findAndAddAllTypes() {
        Task task = new Task("a", "b");
        Epic epic = new Epic("a", "b");
        Subtask subtask = new Subtask("a", "b", epic.getId());

        manager.addEpic(epic);
        manager.addTask(task);
        manager.addSubtask(subtask);

        assertEquals(task, manager.getTaskById(task.getId()));
        assertEquals(epic, manager.getEpicById(epic.getId()));
        assertEquals(subtask, manager.getSubtaskById(subtask.getId()));
    }

    @Test
    void taskShouldNotChangeInManager() {
        Task task1 = new Task("a", "b");
        task1.setStatus(TaskStatus.NEW);

        manager.addTask(task1);

        Task task2 = manager.getTaskById(task1.getId());

        assertEquals(task1.getName(), task2.getName());
        assertEquals(task1.getDescription(), task2.getDescription());
        assertEquals(task2.getStatus(), task1.getStatus());
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        manager.addTask(task);
        int taskId = task.getId();

        final Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void shouldRemoveIdsFromDeletedSubtasks() {
        Epic epic = new Epic("a", "b");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("a", "b", epic.getId());
        manager.addSubtask(subtask);

        manager.removeSubtaskById(subtask.getId());
        assertTrue(epic.getSubtasksIds().isEmpty());

    }

    @Test
    void epicShouldHaveStatusNew(){
        Epic epic = new Epic("a","b");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Sub1", "Desc", epic.getId());
        Subtask subtask2 = new Subtask("Sub2", "Desc", epic.getId());

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void epicShouldHaveStatusDone() {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Sub1", "Desc", epic.getId());
        Subtask subtask2 = new Subtask("Sub2", "Desc", epic.getId());

        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.updateEpicStatus(epic.getId());

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void epicShouldHaveStatusInProgress() {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Sub1", "Desc", epic.getId());
        Subtask subtask2 = new Subtask("Sub2", "Desc", epic.getId());

        subtask1.setStatus(TaskStatus.NEW);
        subtask2.setStatus(TaskStatus.DONE);

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.updateEpicStatus(epic.getId());

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void testEpicStatusInProgress() {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Sub1", "Desc", epic.getId());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);

        manager.addSubtask(subtask1);
        manager.updateEpicStatus(epic.getId());

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }
}