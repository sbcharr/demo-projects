package com.github.sbcharr.examples;

class MyClass {
    long count = 0L;

    synchronized long increment() {
        System.out.println("value of count " + count);
        count++;
        System.out.println("value of count after increment " + count);
        return count;
    }
}

class MyClass2 {
    long count = 0L;

    long increment() {
        synchronized (this) {
            System.out.println("value of count " + count);
            count++;
            System.out.println("value of count after the increment " + count);
            return count;
        }
    }
}
