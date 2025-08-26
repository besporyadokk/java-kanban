
package com.yandex.fz4.service;

import com.yandex.fz4.model.*;

import java.util.*;
import java.io.*;
import java.nio.file.*;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic");
            for (Task task : getTasks()) {
                writer.write(toString(task));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String toString(Task task) {
        if (task.getTaskType() == TaskType.TASK) {
            return task.getId() + ",TASK," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription() + ",";
        } else if (task.getTaskType() == TaskType.EPIC) {
            return task.getId() + ",EPIC," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription() + ",";
        } else {
            return task.getId() + ",SUBTASK," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription() + ",";
        }
    }
}
