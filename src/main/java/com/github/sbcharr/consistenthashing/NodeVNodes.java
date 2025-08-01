package com.github.sbcharr.consistenthashing;

import java.util.List;

public class NodeVNodes {
    private final Node node;
    private final List<Long> vnodeList;

    NodeVNodes(Node node, List<Long> vnodeList) {
        this.node = node;
        this.vnodeList = vnodeList;
    }

    Node getNode() {
        return node;
    }

    List<Long> getVnodeList() {
        return vnodeList;
    }

    @Override
    public String toString() {
        return "NodevnodeListPair{" +
                "node=" + node.getHostname() +
                ", vnodeList=" + vnodeList +
                '}';
    }
}
