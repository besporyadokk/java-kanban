package com.yandex.fz4.model;


import java.util.ArrayList;


public class Epic extends Task {
    private ArrayList<Integer> subtasksIds;


    public Epic(String name, String description) {
        super(name, description);
        this.subtasksIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksIds() {
        return new ArrayList<>(subtasksIds);
    }

    public void addSubtaskId(int subtaskId) {
        if (subtaskId != this.id) {
            subtasksIds.add(subtaskId);
        }
    }

    public void removeSubtaskId(int subtaskId) {
        subtasksIds.remove(Integer.valueOf(subtaskId));
    }


    @Override
    public String toString() {
        return id + "," +
                type + "," +
                name + "," +
                status + "," +
                description;
    }

}
