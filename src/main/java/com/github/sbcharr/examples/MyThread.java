package com.github.sbcharr.examples;

// Thread creation by extending Thread class
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread by extending the Thread class");
    }
}

// Thread creation by implementing Runnable interface
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Thread by implementing the Runnable interface");
    }
}

class MainApp {
    public static void main(String[] args) {
        // Thread via extending Thread class
        MyThread thread1 = new MyThread();
        thread1.start();

        // Thread via implementing Runnable interface
        Thread thread2 = new Thread(new MyRunnable());
        thread2.start();
    }
}

