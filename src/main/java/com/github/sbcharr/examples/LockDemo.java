package com.github.sbcharr.examples;

// Example: Bank Account with non-volatile 'balance' (Bad Practice)
class BankAccount {
    private int balance = 0; // shared mutable state
    private final Object lock = new Object();

    public void deposit(int amount) {
        synchronized (lock) {
            balance += amount;
        }
    }

    public void withdraw(int amount) {
        synchronized (lock) {
            balance -= amount;
        }
    }

    public int getBalance() {
        synchronized (lock) {
            return balance;
        }
    }
}

// Demonstration of Failure
public class LockDemo {
    public static void main(String[] args) throws InterruptedException {
        BankAccount account = new BankAccount();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                account.deposit(1);
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                account.withdraw(1);
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Final balance=" + account.getBalance());
    }
}

// Expected output: 0
// Possible output: Any random number, because both threads update balance without mutual exclusion.

// Correct Approach: Use 'volatile' for visibility of the shared mutable state
class SafeBankAccount {
    private volatile int balance = 0;
    private final Object lock = new Object();

    public void deposit(int amount) {
        synchronized (lock) {
            balance += amount;
        }
    }

    public void withdraw(int amount) {
        synchronized (lock) {
            balance -= amount;
        }
    }

    public int getBalance() {
        synchronized (lock) {
            return balance;
        }
    }
}
// Now `deposit()` and `withdraw()` will get a consistent view of `balance`.



