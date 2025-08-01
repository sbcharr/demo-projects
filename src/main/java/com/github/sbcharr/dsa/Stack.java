package com.github.sbcharr.dsa;

import javax.annotation.Nullable;
import java.util.List;
import java.util.ArrayList;

public class Stack {
    List<Integer> stack;

    public Stack() {
        stack = new ArrayList<>();
    }

    public boolean push(int item) {
        return stack.add(item);
    }

    public @Nullable Integer pop() {
        if (stack.isEmpty()) {
            return null;
        }

        return stack.remove(size() - 1);
    }

    public @Nullable Integer peek() {
        if (stack.isEmpty()) {
            return null;
        }

        return stack.get(size() - 1);
    }

    public int size() {
        return stack.size();
    }
}
