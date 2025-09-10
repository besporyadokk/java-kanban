package com.yandex.fz4.service;

import com.yandex.fz4.model.*;

import java.util.*;

public interface TaskManager {

    List<Task> getHistory();


    /// Task
    ArrayList<Task> getTasks();

    void clearTasks();

    void addTask(Task task);

    Task getTaskById(int id);

    void updateTask(Task task);

    void removeTaskById(int id);

    /// Epic

    ArrayList<Epic> getEpics();

    void clearEpics();

    void addEpic(Task epic);

    Epic getEpicById(int id);

    void updateEpic(Task epic);

    void removeEpicById(int id);

    /// Subtask

    ArrayList<Subtask> getSubtasks();

    void clearSubtasks();

    void addSubtask(Task subtask);

    Subtask getSubtaskById(int id);

    void updateSubtask(Task subtask);

    void removeSubtaskById(int id);

    void updateEpicStatus(int epicId);

    ArrayList<Subtask> getSubtasksByEpicId(int epicId);
}
