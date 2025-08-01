package com.github.sbcharr.concurrency.boundedbuffer;

public class BlockingQueue<T> {
    private final T[] queue;
    private int size;
    private final int capacity;
    private int head = 0;
    private int tail = 0;
    private final Object lock = new Object();

    @SuppressWarnings("unchecked")
    public BlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0");
        }
        this.queue = (T[]) new Object[capacity];
        this.capacity = capacity;
    }

    public void enqueue(T item) throws InterruptedException {
        synchronized (lock) {
            while (size == capacity) {
                lock.wait();
            }
            queue[tail] = item;
            tail = (tail + 1) % capacity;
            size++;

            lock.notifyAll();
        }
    }

    public T dequeue() throws InterruptedException {
        synchronized (lock) {
            while (size == 0) {
                lock.wait();
            }
            T item = queue[head];
            queue[head] = null;
            head = (head + 1) % capacity;
            size--;

            lock.notifyAll();
            return item;
        }
    }
}

class AppMain {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(10);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 30; i++) {
                try {
                    queue.enqueue(i);
                    System.out.println("thread 1 enqueued -> " + i);
                } catch (InterruptedException ex) {
                    System.out.println("exception: " + ex.getMessage());
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 15; i++) {
                try {
                    queue.dequeue();
                    System.out.println("thread 2 dequeued -> " + i);
                } catch (InterruptedException ex) {
                    System.out.println("exception: " + ex.getMessage());
                }
            }
        });

        Thread t3 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    queue.dequeue();
                    System.out.println("thread 3 dequeued -> " + i);
                } catch (InterruptedException ex) {
                    System.out.println("exception: " + ex.getMessage());
                }
            }
        });

        t1.start();
        Thread.sleep(3000);
        t2.start();
        t2.join();
        t3.start();
        t1.join();
        t3.join();
    }
}
