package com.yandex.fz4.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.yandex. fz4.model.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager manager;

    @BeforeEach
    void setUp(){
        manager = new InMemoryTaskManager();
    }

    @Test
    void differentMadeIdsDoNotConflict(){
        Task task1 = new Task("a","b");
        manager.addTask(task1);
        Task task2 = new Task("b","a");
        task2.setId(3);
        manager.addTask(task2);
        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void findAndAddAllTypes(){
        Task task = new Task("a","b");
        Epic epic = new Epic("a","b");
        Subtask subtask = new Subtask("a","b",epic.getId());

        manager.addEpic(epic);
        manager.addTask(task);
        manager.addSubtask(subtask);

        assertEquals(task,manager.getTaskById(task.getId()));
        assertEquals(epic,manager.getEpicById(epic.getId()));
        assertEquals(subtask,manager.getSubtaskById(subtask.getId()));
    }

    @Test
    void TaskShouldNotChangeInManager(){
        Task task1 = new Task("a","b");
        task1.setStatus(TaskStatus.NEW);

        manager.addTask(task1);

        Task task2 = manager.getTaskById(task1.getId());

        assertEquals(task1.getName(),task2.getName());
        assertEquals(task1.getDescription(),task2.getDescription());
        assertEquals(task2.getStatus(),task1.getStatus());
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        task.setId(1);
        int taskId = task.getId();

        final Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }
}