package com.yandex.fz4.service;

import com.yandex.fz4.model.Task;
import com.yandex.fz4.model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {


    @Test
    void historyShouldSaveTasks(){
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("a","b");
        task1.setId(1);
        task1.setStatus(TaskStatus.NEW);

        historyManager.add(task1);

        Task task2 = historyManager.getHistory().getFirst();

        assertEquals(task1.getName(),task2.getName());
        assertEquals(task1.getDescription(),task2.getDescription());
        assertEquals(task1.getStatus(),task2.getStatus());

    }

    @Test
    void add() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("a","b");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }
}