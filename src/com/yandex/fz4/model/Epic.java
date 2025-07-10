package com.yandex.fz4.model;

import java.util.ArrayList;



public class Epic extends Task{
    private ArrayList<Integer> subtasksIds;


    public Epic(String name,String description){
        super(name,description);
        this.subtasksIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksIds(){
        return new ArrayList<>(subtasksIds);
    }

    public void addSubtaskId(int subtaskId){
        subtasksIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId){

        subtasksIds.remove(Integer.valueOf(subtaskId));
    }


    @Override
    public String toString(){
        return "com.yandex.fz4.model.Epic{" +
                "name = '" + getName() + '\''+
                ", description = " + getDescription() + '\'' +
                ", status = " + getStatus() + '\'' +
                ", id = '" + getId() +
                "', subtaskIds = '" + getSubtasksIds() +
                '}';
    }

}
