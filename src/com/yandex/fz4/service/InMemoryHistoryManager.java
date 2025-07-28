package com.yandex.fz4.service;

import com.yandex.fz4.model.*;


import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_SIZE = 10;

    private LinkedList<Task> viewHistory = new LinkedList<>();

    @Override
    public List<Task> getHistory() {
        viewHistory = new LinkedList<>(viewHistory);
        if (viewHistory.size() > MAX_SIZE) {
            viewHistory.removeLast();
        }
        return viewHistory;
    }

    @Override
    public void add(Task task) {
        if (task == null) return;

        viewHistory.addFirst(task);
        if (viewHistory.size() > MAX_SIZE) {
            viewHistory.removeLast();
        }
    }
}
