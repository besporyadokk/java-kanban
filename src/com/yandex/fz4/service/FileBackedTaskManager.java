
package com.yandex.fz4.service;

import com.yandex.fz4.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;


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
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private String toString(Task task) {
        if (task.getTaskType() == TaskType.TASK) {
            return task.getId() + ",TASK," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription() + " ";
        } else if (task.getTaskType() == TaskType.EPIC) {
            return task.getId() + ",EPIC," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription() + " ";
        } else {
            Subtask subtask = (Subtask) task;
            return subtask.getId() + ",SUBTASK," + subtask.getName() + "," +
                    subtask.getStatus() + "," + subtask.getDescription() + "," + subtask.getEpicId();
        }
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    public static Task fromString(String string) {
        String[] split = string.split(",");
        if (split.length < 5 | split.length > 6) {
            return null;
        }
        int id = Integer.parseInt(split[0]);
        String type = split[1];
        String name = split[2];
        TaskStatus status = TaskStatus.valueOf(split[3]);
        String description = split[4];
        if (split.length == 6) {
            int epicId = Integer.parseInt(split[5]);
            Subtask subtask = new Subtask(name, description, epicId);
            subtask.setId(id);
            subtask.setStatus(status);
            return subtask;
        } else {
            if (type.equals("TASK")) {
                Task task = new Task(name, description);
                task.setId(id);
                task.setStatus(status);
                return task;
            } else if (type.equals("EPIC")) {
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            } else {
                return null;
            }
        }

    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            if (!file.exists() || file.length() == 0) {
                return manager;
            }

            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() <= 1) return manager;

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;

                Task task = fromString(line);
                if (task != null) {
                    if (task instanceof Epic) {
                        manager.getEpicsMap().put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        manager.getSubtasksMap().put(task.getId(), (Subtask) task);
                        // Добавляем ID подзадачи в эпик
                        Subtask subtask = (Subtask) task;
                        Epic epic = manager.getEpicsMap().get(subtask.getEpicId());
                        if (epic != null) {
                            epic.addSubtaskId(subtask.getId());
                        }
                    } else {
                        manager.getTasksMap().put(task.getId(), task);
                    }

                    if (task.getId() >= manager.getNewId()) {
                        manager.setNewId(task.getId() + 1);
                    }
                }
            }
            for (Epic epic : manager.getEpicsMap().values()) {
                manager.updateEpicStatus(epic.getId());
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла: " + e.getMessage());
        }

        return manager;
    }
}

