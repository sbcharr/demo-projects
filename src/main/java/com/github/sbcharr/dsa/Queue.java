package com.github.sbcharr.dsa;

import javax.annotation.Nullable;

public class Queue {
    LinkedList queue;

    public Queue() {
        queue = new LinkedList();
    }

    public void push(int value) {
        queue.addNodeToTail(new LinkedListNode(value));
    }

    public Integer pop() {
        LinkedListNode head = queue.removeNodeFromHead();

        return head == null ? null : head.value;
    }

    public Integer peek() {
        LinkedListNode head = queue.getFirstNode();

        return head == null ? null : head.value;
    }

    public int size() {
        return queue.size();
    }

    class LinkedListNode {
        int value;
        LinkedListNode next;

        LinkedListNode(int value) {
            this.value = value;
            this.next = null;
        }
    }

    class LinkedList {
        LinkedListNode head;
        LinkedListNode tail;
        int totalNodes;

        void addNodeToTail(LinkedListNode node) {
            if (head == null) {
                head = node;
                tail = head;
            } else {
                tail.next = node;
                tail = tail.next;
            }
            totalNodes++;
        }

        @Nullable LinkedListNode removeNodeFromHead() {
            if (head == null) {
                return null;
            }

            LinkedListNode currHead = head;
            head = head.next;

            totalNodes--;

            return currHead;
        }

        @Nullable LinkedListNode getFirstNode() {
            return head == null ? null : head;
        }

        int size() {
            return totalNodes;
        }
    }
}
