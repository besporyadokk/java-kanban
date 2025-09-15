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
        int id = task.getId();
        remove(id);
        linkLast(task);
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

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
            if (node.next == null) {
                last = node.prev;
                last.next = null;
            } else {
                node.next.prev = node.prev;
            }
        } else {
            first = node.next;
            if (first == null) {
                last = null;
            } else {
                first.prev = null;
            }
        }
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.remove(id);
        removeNode(node);
    }





}
