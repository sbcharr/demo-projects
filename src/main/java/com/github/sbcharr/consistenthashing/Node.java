package com.github.sbcharr.consistenthashing;

import java.util.Objects;

public class Node {
    private final String hostname;

    public Node(String hostname) {
        this.hostname = hostname;
    }

    public String getHostname() {
        return hostname;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(hostname, node.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(hostname);
    }

    @Override
    public String toString() {
        return "Node{" +
                "hostname='" + hostname + '\'' +
                '}';
    }
}
