package com.github.sbcharr.concurrency.boundedbuffer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueueWithExplicitLock<T> {
    private final T[] queue; // A circular queue using fixed size array
    private int size;
    private final int capacity;
    private int head = 0;
    private int tail = 0;
    private final Lock lock = new ReentrantLock();

    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    @SuppressWarnings("unchecked")
    public BlockingQueueWithExplicitLock(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0");
        }
        this.queue = (T[]) new Object[capacity];
        this.capacity = capacity;
    }

    public void enqueue(T item) throws InterruptedException, NullPointerException {
        if (item == null) {
            throw new NullPointerException("null item not allowed");
        }
        lock.lock();
        try {
            while (size == capacity) {
                notFull.await();
            }
            queue[tail] = item;
            tail = (tail + 1) % capacity;
            size++;

            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public T dequeue() throws InterruptedException {
        T item;
        lock.lock();
        try {
            while (size == 0) {
                notEmpty.await();
            }
            item = queue[head];
            queue[head] = null;
            head = (head + 1) % capacity;
            size--;

            notFull.signalAll();
        } finally {
            lock.unlock();
        }
        return item;
    }

    public static void main(String[] args) throws InterruptedException {
        BlockingQueueWithExplicitLock<Integer> queue = new BlockingQueueWithExplicitLock<>(10);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 30; i++) {
                try {
                    queue.enqueue(i);
                    System.out.println("thread 1 enqueued -> " + i);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 15; i++) {
                try {
                    Integer item = queue.dequeue();
                    System.out.println("thread 2 dequeued -> " + item);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Thread t3 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Integer item = queue.dequeue();
                    System.out.println("thread 3 dequeued -> " + item);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });

        t1.start();
        TimeUnit.SECONDS.sleep(2);
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();
    }
}

