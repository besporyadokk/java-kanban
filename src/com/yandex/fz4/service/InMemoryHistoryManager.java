package com.yandex.fz4.service;

import com.yandex.fz4.model.*;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_SIZE = 10;

    private LinkedList<Task> viewHistory = new LinkedList<>();

    private static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task, Node prev, Node next) {
            this.next = next;
            this.task = task;
            this.prev = prev;
        }

    }

    private Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node current = first;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }


    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        final int id = task.getId();
        nodeMap.put(id, last);
    }

    private void linkLast(Task task) {
        Node newNode = new Node(task, last, null);
        if (last == null) {
            first = newNode;

        } else {
            last.next = newNode;
        }
        last = newNode;
        nodeMap.put(task.getId(), newNode);
    }

    private void removeNode(int id) {
        final Node node = nodeMap.remove(id);
        if (node == first) {
            first = node.next;
            first.prev = null;
        } else if (node == last) {
            last = node.next;
            last.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    /*@Override
    public void add(Task task) {
        if (task == null) return;

        viewHistory.addFirst(task);
        if (viewHistory.size() > MAX_SIZE) {
            viewHistory.removeLast();
        }
    }*/
    @Override
    public void remove(int id) {
        removeNode(id);
    }

}
