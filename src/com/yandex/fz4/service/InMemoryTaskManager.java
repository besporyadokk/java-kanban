package com.yandex.fz4.service;

import com.yandex.fz4.model.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int newId = 0;

    private InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    protected HashMap<Integer, Task> getTasksMap() {
        return tasks;
    }

    protected HashMap<Integer, Epic> getEpicsMap() {
        return epics;
    }

    protected HashMap<Integer, Subtask> getSubtasksMap() {
        return subtasks;
    }

    //private TreeSet<Task> prioritizedTasks = new TreeSet<>()

    protected int getNewId() {
        return newId;
    }

    protected void setNewId(int newId) {
        this.newId = newId;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
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
        if (isTaskCrossWithAnyOther(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой.");
        }
        task.setId(newId++);
        task.setTaskType(TaskType.TASK);
        tasks.put(task.getId(), task);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        if (isTaskCrossWithAnyOther(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой.");
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    //EPIC-------------------------------------------
    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void addEpic(Task epic) {
        epic.setId(newId++);
        epic.setTaskType(TaskType.EPIC);
        epics.put(epic.getId(), (Epic) epic);
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void updateEpic(Task epic) {
        epics.put(epic.getId(), (Epic) epic);
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
    public void addSubtask(Task task) {
        Subtask subtask = (Subtask) task;

        if (isTaskCrossWithAnyOther(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой.");
        }

        if (!epics.containsKey(subtask.getEpicId())) {
            return;
        }
        subtask.setId(newId++);
        subtask.setTaskType(TaskType.SUBTASK);
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
        subtask.setStatus(TaskStatus.NEW);
        updateEpicStatus(subtask.getEpicId());

    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void updateSubtask(Task task) {
        Subtask subtask = (Subtask) task;

        if (isTaskCrossWithAnyOther(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой.");
        }

        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtaskId(id);
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
        epic.setDuration(getEpicDuration(epicId));
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        return getEpicById(epicId).getSubtasksIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean isTasksCross(Task task1, Task task2) {
        if (task1.toString().equals(task2.toString())) {
            return false;
        }
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    public TreeSet<Task> getPrioritizedTasks() {
        Comparator<Task> comparator = Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()));
        TreeSet<Task> prioritizedTasks = new TreeSet<>(comparator);

        prioritizedTasks.addAll(new ArrayList<>(tasks.values()));
        return prioritizedTasks;
    }

    public boolean isTaskCrossWithAnyOther(Task task) {
        return getPrioritizedTasks().stream()
                .anyMatch(t -> isTasksCross(t, task));
    }

    public LocalDateTime getEpicStartTime(int epicId) {
        return getSubtasksByEpicId(epicId).stream()
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    public LocalDateTime getEpicEndTime(int epicId) {
        return getSubtasksByEpicId(epicId).stream()
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    public long getEpicDuration(int epicId) {
        return getSubtasksByEpicId(epicId).stream()
                .mapToLong(Subtask::getDuration)
                .sum();
    }

}
