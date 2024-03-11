package ru.yandex.practicum.taskmanager.manager;

import ru.yandex.practicum.taskmanager.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;

    private final Map<Integer, Node<Task>> nodeMap = new HashMap<>();

    private static class Node<T> {
        public Node<T> prev;
        public Node<T> next;
        public T data;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }

    private void linkLast(Task task) {
        final Node<Task> newNode = new Node<>(tail, task, null);
        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
        nodeMap.put(task.getId(), newNode);
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            tasks.add(node.data);
            node = node.next;
        }
        return tasks;
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            if (node == head && node == tail) {
                head = null;
                tail = null;
            } else if (node == head) {
                node.next.prev = null;
                head = node.next;
            } else if (node == tail) {
                node.prev.next = null;
                tail = node.prev;
            } else {
                node.next.prev = node.prev;
                node.prev.next = node.next;
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        removeNode(nodeMap.get(id));
        nodeMap.remove(id);
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (nodeMap.containsKey(task.getId())) {
            removeNode(nodeMap.get(task.getId()));
        }
        linkLast(task);
    }
}