package com.github.sbcharr;

import java.util.HashMap;
import java.util.Map;

class LRUCache {
    private int capacity;
    private int length;
    private Map<Integer, ListNode> nodeMap;
    private DoublyLinkedList cache;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.nodeMap = new HashMap<>();
        this.cache = new DoublyLinkedList();
    }

    public int get(int key) {
        if (!nodeMap.containsKey(key)) {
            return -1;
        }

        ListNode node = nodeMap.get(key);
        cache.rebalanceNode(node);

        return node.value.value;
    }

    public void put(int key, int value) {
        if (nodeMap.containsKey(key)) {
            ListNode node = nodeMap.get(key);
            node.value.value = value;
            cache.rebalanceNode(node);
        } else {
            if (length == capacity) {
                ListNode tail = cache.getTail();
                nodeMap.remove(tail.value.key);
                cache.deleteAtTail();
            }
            ListNode newNode = new ListNode(new Pair(key, value));
            cache.addNode(newNode);
            nodeMap.put(key, newNode);
            length++;
        }
    }

    public DoublyLinkedList getCache() {
        return this.cache;
    }
}

class ListNode {
    Pair value;
    ListNode next;
    ListNode prev;

    public ListNode(Pair value) {
        this.value = value;
        this.next = null;
        this.prev = null;
    }
}

class DoublyLinkedList {
    private ListNode head;
    private ListNode tail;

    public ListNode getHead() {
        return this.head;
    }

    public ListNode getTail() {
        return this.tail;
    }

    public void addNode(ListNode node) {
        if (head == null) {
            head = node;
            tail = head;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
    }

    public void rebalanceNode(ListNode node) {
        if (node != head) {
            node.prev.next = node.next;
            if (node == tail) {
                tail = tail.prev;
            } else {
                node.next.prev = node.prev;
            }
            node.prev = node.next = null;
            addNode(node);
        }
    }

    public void deleteAtTail() {
        if (tail != head) {
            tail.prev.next = tail.next;
            tail = tail.prev;
            tail.prev = null;
        } else {
            head = tail = null;
        }
    }
}

class Pair {
    int key;
    int value;

    public Pair(int key, int value) {
        this.key = key;
        this.value = value;
    }
}

