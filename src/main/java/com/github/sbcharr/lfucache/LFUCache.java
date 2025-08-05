package com.github.sbcharr.lfucache;


import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

class Node {
    int key, value, freq;

    public Node(int key, int value) {
        this.key = key;
        this.value = value;
        this.freq = 1;
    }
}

public class LFUCache {
    Map<Integer, Node> keyToNode;
    Map<Integer, LinkedHashSet<Node>> freqToNodes;
    int capacity, minFreq;

    public LFUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity should be positive integer");
        }
        this.keyToNode = new HashMap<>();
        this.freqToNodes = new HashMap<>();
        this.capacity = capacity;
        minFreq = 0;
    }

    public int get(int key) {
        if (!keyToNode.containsKey(key)) {
            return -1;
        }
        Node node = keyToNode.get(key);
        updateFrequency(key);

        return node.value;
    }

    public void put(int key, int value) {
        if (keyToNode.containsKey(key)) {
            Node node = keyToNode.get(key);
            node.value = value;

            updateFrequency(key);
        } else {
            if (keyToNode.size() == capacity) {
                evict();
            }
            addNode(key, value);
            minFreq = 1;
        }
    }

    private void updateFrequency(int key) {
        Node node = keyToNode.get(key);
        int oldCount = node.freq;
        freqToNodes.get(oldCount).remove(node);
        if (freqToNodes.get(oldCount).isEmpty()) {
            freqToNodes.remove(oldCount);
            if (minFreq == oldCount) {
                minFreq++;
            }
        }
        node.freq++;
        freqToNodes.computeIfAbsent(node.freq, k -> new LinkedHashSet<>()).add(node);
    }

    private void addNode(int key, int value) {
        Node node = new Node(key, value);
        keyToNode.put(key, node);
        freqToNodes.computeIfAbsent(node.freq, k -> new LinkedHashSet<>()).add(node);
    }

    private void evict() {
        Node oldest = freqToNodes.get(minFreq).iterator().next();
        keyToNode.remove(oldest.key);
        freqToNodes.get(minFreq).remove(oldest);
        if (freqToNodes.get(minFreq).isEmpty()) {
            freqToNodes.remove(minFreq);
        }
    }
}

