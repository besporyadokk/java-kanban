package com.yandex.fz4.model;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        if (epicId != this.id) { // проверка при создании
            this.epicId = epicId;
        } else {
            this.epicId = 0; // или другое значение по умолчанию
        }
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        if (epicId != this.id) {
            this.epicId = epicId;
        }
    }

    @Override
    public String toString() {
        return
                getName() + ", " +
                        getDescription() + ", " +
                        ", " + getStatus() +
                        ", " + getId() + ", " + getEpicId();
    }

}
