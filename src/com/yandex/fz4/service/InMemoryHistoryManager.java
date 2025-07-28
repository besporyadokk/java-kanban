package com.yandex.fz4.service;

import com.yandex.fz4.model.*;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private ArrayList<Task> viewHistory = new ArrayList<>(10);

    @Override
    public List<Task> getHistory(){
        viewHistory = new ArrayList<>(viewHistory);
        if (viewHistory.size() > 10){
            viewHistory.removeLast();
        }
        return viewHistory;
    }
    @Override
    public void add(Task task){
        viewHistory.add(task);
    }
}
