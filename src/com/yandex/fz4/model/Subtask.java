package com.yandex.fz4.model;

public class Subtask extends Task{

    private int epicId;

    public Subtask(String name, String description,int epicId){
        super(name,description);
        this.epicId = epicId;
    }

    public int getEpicId(){
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public String toString(){
        return "com.yandex.fz4.model.Subtask{" +
                "name = '" + name + '\''+
                ", description = " + description + '\'' +
                ", status = " + status + '\'' +
                "id = " + id + '}';
    }

}
