
package com.yandex.fz4.service;

import com.yandex.fz4.model.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;
    private static final String toStringType = "id,type,name,status,description,epicId,startTime,endTime,duration";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(toStringType);
            writer.newLine();
            for (Task task : getTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private String toString(Task task) {
        return task.toString();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public Task addTask(Task task) {
        Task result = super.addTask(task); // должен увеличить newId
        save();
        return result;
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
    public Epic addEpic(Task epic) {
        Epic result = super.addEpic(epic); // должен увеличить newId
        save();
        return result;
    }

    @Override
    public void updateEpic(Task epic) {
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
    public Subtask addSubtask(Task subtask) {
        Subtask result = super.addSubtask(subtask); // должен увеличить newId
        save();
        return result;
    }

    @Override
    public void updateSubtask(Task subtask) {
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
        if (split.length < 5) {
            return null;
        }

        int id = Integer.parseInt(split[0]);
        String type = split[1];
        String name = split[2];
        TaskStatus status = TaskStatus.valueOf(split[3]);
        String description = split[4];

        Task task = null;

        if (type.equals("SUBTASK")) {
            if (split.length < 6) return null;
            int epicId = Integer.parseInt(split[5]);
            Subtask subtask = new Subtask(name, description, epicId);
            subtask.setId(id);
            subtask.setStatus(status);
            task = subtask;
        } else if (type.equals("TASK")) {
            Task simpleTask = new Task(name, description);
            simpleTask.setId(id);
            simpleTask.setStatus(status);
            task = simpleTask;
        } else if (type.equals("EPIC")) {
            Epic epic = new Epic(name, description);
            epic.setId(id);
            epic.setStatus(status);
            task = epic;
        } else {
            return null;
        }


        if (split.length > 6 && !"null".equals(split[6]) && !split[6].isEmpty()) {
            LocalDateTime startTime = LocalDateTime.parse(split[6]);
            task.setStartTime(startTime);
        }

        if (split.length > 7 && !"null".equals(split[7]) && !split[7].isEmpty()) {
            LocalDateTime endTime = LocalDateTime.parse(split[7]);
            task.setEndTime(endTime);
        }

        if (split.length > 8 && !"null".equals(split[8]) && !split[8].isEmpty()) {
            long duration = Long.parseLong(split[8]);
            task.setDuration(duration);
        }

        return task;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            if (!file.exists() || file.length() == 0) {
                return manager;
            }

            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
            if (lines.size() <= 1) return manager;

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                Task task = fromString(line);
                if (task != null) {
                    if (task.getTaskType().equals(TaskType.EPIC)) {
                        manager.addEpicWithoutSave((Epic) task);
                    } else if (task.getTaskType().equals(TaskType.SUBTASK)) {
                        manager.addSubtaskWithoutSave((Subtask) task);

                        Subtask subtask = (Subtask) task;
                        Epic epic = manager.getEpicById(subtask.getEpicId());
                        if (epic != null) {
                            epic.addSubtaskId(subtask.getId());
                        }
                    } else {
                        manager.addTaskWithoutSave(task);
                    }

                    if (task.getId() >= manager.getNewId()) {
                        manager.setNewId(task.getId() + 1);
                    }
                }
            }
            for (Epic epic : manager.getEpics()) {
                manager.updateEpicStatus(epic.getId());
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла: " + e.getMessage());
        }

        return manager;
    }

    private void addTaskWithoutSave(Task task) {
        super.addTask(task);
    }

    private void addEpicWithoutSave(Epic epic) {
        super.addEpic(epic);
    }

    private void addSubtaskWithoutSave(Subtask subtask) {
        super.addSubtask(subtask);
    }

}

