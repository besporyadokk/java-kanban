package com.yandex.fz4.service;

import com.yandex.fz4.model.Epic;
import com.yandex.fz4.model.Subtask;
import com.yandex.fz4.model.Task;
import com.yandex.fz4.model.TaskStatus;

import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int newId = 0;
    private ArrayList<Task> viewHistory = new ArrayList<>(10);

    @Override
    public List<Task> getHistory(){
        viewHistory = new ArrayList<>(viewHistory);
        if (viewHistory.size() > 10){
            viewHistory.removeLast();
        }
        return viewHistory;
    }



    //TASK--------------------------------------------------------
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void addTask(Task task) {
        task.setId(newId++);
        tasks.put(task.getId(), task);
    }

    @Override
    public Task getTaskById(int id) {
        viewHistory.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    //EPIC-------------------------------------------
    @Override
    public ArrayList<Epic> getEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(newId++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public Epic getEpicById(int id) {
        viewHistory.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void removeEpicById(int id) {
        for (int subtaskId : getEpicById(id).getSubtasksIds()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    //SUBTASK---------------------------------------------------
    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasksIds().clear();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();

    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            return;
        }
        subtask.setId(newId++);
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
        subtask.setStatus(TaskStatus.NEW);
        updateEpicStatus(subtask.getId());

    }

    @Override
    public Subtask getSubtaskById(int id) {
        viewHistory.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasksIds().remove((Integer) id);
        updateEpicStatus(epic.getId());
        subtasks.remove(id);
    }

    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        boolean allNew = true;
        boolean allDone = true;

        for (int subtaskId : epic.getSubtasksIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) continue;

            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }

    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        for (int SubtaskId : epics.get(epicId).getSubtasksIds()) {
            subtasksList.add(subtasks.get(SubtaskId));
        }
        return subtasksList;
    }
}
