package com.github.sbcharr.consistenthashing;

import com.github.sbcharr.util.Utils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class HashingRing {
    private final NavigableMap<Long, Node> vnodeToPhysicalNodeMap = new ConcurrentSkipListMap<>();
    private final Map<Node, List<Long>> physicalNodeToVnodesMap = new ConcurrentHashMap<>();
    private final Map<String, Node> shardToNodeMap = new ConcurrentHashMap<>(); // In production systems this can be stored in a cache such as 'Redis'
    private final Map<Node, Set<String>> nodeToShardListMap = new ConcurrentHashMap<>();

    private final long ringSize;
    private final int numVirtualNodes; // Number of virtual nodes
    private final Hashable hashable;


    public HashingRing(long ringSize, int numVirtualNodes, Hashable hashable) {
        this.ringSize = ringSize;
        this.numVirtualNodes = numVirtualNodes;
        this.hashable = hashable;
    }

    public NodeVNodes addNode(Node node) throws IllegalArgumentException {
        if (node == null) {
            throw new IllegalArgumentException("node cannot be null");
        }
        if (node.getHostname() == null || node.getHostname().isEmpty()) {
            throw new IllegalArgumentException("node hostname cannot be null or empty");
        }
        if (!physicalNodeToVnodesMap.isEmpty() && physicalNodeToVnodesMap.containsKey(node)) {
            throw new IllegalArgumentException("node already exists in the ring");
        }

        List<Long> vnodeList = new ArrayList<>();

        for (int i = 0; i < this.numVirtualNodes; i++) {
            StringBuilder virtualNodeId = new StringBuilder(node.getHostname() + "#" + i);
            long nodePositionOnRing = this.hashable.hash(virtualNodeId.toString(), ringSize);

            while (vnodeToPhysicalNodeMap.containsKey(nodePositionOnRing)) {
                virtualNodeId.append("#");
                nodePositionOnRing = this.hashable.hash(virtualNodeId.toString(), ringSize);
            }
            vnodeToPhysicalNodeMap.put(nodePositionOnRing, node);
            vnodeList.add(nodePositionOnRing);
        }
        physicalNodeToVnodesMap.put(node, vnodeList);

        return new NodeVNodes(node, vnodeList);
    }

    public @Nullable NodeVNodes removeNode(Node node) throws IllegalArgumentException {
        if (node == null) {
            throw new IllegalArgumentException("node cannot be null");
        }
        if (node.getHostname() == null || node.getHostname().isEmpty()) {
            throw new IllegalArgumentException("node hostname cannot be null or empty");
        }

        if (physicalNodeToVnodesMap.containsKey(node)) {
            List<Long> vnodeList = physicalNodeToVnodesMap.get(node);
            for (Long vnode : vnodeList) {
                vnodeToPhysicalNodeMap.remove(vnode);
            }
            physicalNodeToVnodesMap.remove(node);

            return new NodeVNodes(node, vnodeList);
        }

        return null;
    }

    public void clearRing() {
        vnodeToPhysicalNodeMap.clear();
        physicalNodeToVnodesMap.clear();
    }

    public @Nullable Node getNodeForShard(String shardId) throws RuntimeException {
        if (shardId == null || shardId.isEmpty()) {
            throw new IllegalArgumentException("shardId cannot be null or empty");
        }
        if (vnodeToPhysicalNodeMap.isEmpty()) {
            throw new RuntimeException("No nodes in the ring");
        }

        long shardPositionOnRing = this.hashable.hash(shardId, ringSize);
        Map.Entry<Long, Node> entry = vnodeToPhysicalNodeMap.ceilingEntry(shardPositionOnRing);
        if (entry != null) {
            return entry.getValue();
        }
        // If no entry is found, return the first node
        return vnodeToPhysicalNodeMap.firstEntry().getValue();
    }

    public @Nullable Node getNodeForExistingShard(String shardId) throws IllegalArgumentException {
        if (Utils.isNullOrEmpty(shardId)) {
            throw new IllegalArgumentException("shardId cannot be null or empty");
        }

        return shardToNodeMap.get(shardId);
    }

    public Node addShardToNode(String shardId) throws IllegalArgumentException {
        if (Utils.isNullOrEmpty(shardId)) {
            throw new IllegalArgumentException("shardId cannot be null or empty");
        }
        if (shardToNodeMap.containsKey(shardId)) {
            throw new IllegalArgumentException("shardId already exists in the ring");
        }

        Node node;
        try {
            node = getNodeForExistingShard(shardId);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("shardId does not exist in the ring");
        } catch (RuntimeException ex) {
            throw new RuntimeException("No nodes in the ring");
        }

        shardToNodeMap.put(shardId, node);
        Set<String> shardList = nodeToShardListMap.computeIfAbsent(node, k -> Collections.synchronizedSet(new HashSet<>()));
        shardList.add(shardId);
        nodeToShardListMap.put(node, shardList);

        return node;
    }

    public void removeShardFromNode(String shardId) throws IllegalArgumentException {
        if (Utils.isNullOrEmpty(shardId)) {
            throw new IllegalArgumentException("shardId cannot be null or empty");
        }
        if (!shardToNodeMap.containsKey(shardId)) {
            throw new IllegalArgumentException("shardId does not exist in the ring");
        }
        Node node = shardToNodeMap.get(shardId);
        if (node != null) {
            Set<String> shardList = nodeToShardListMap.get(node);
            if (shardList != null) {
                shardList.remove(shardId);
            }
            shardToNodeMap.remove(shardId);
        }
    }

    public void removeAllShardsFromNode(Node node) throws IllegalArgumentException {
        if (Utils.isNullOrEmpty(node)) {
            throw new IllegalArgumentException("node cannot be null or empty");
        }
        if (!nodeToShardListMap.containsKey(node)) {
            throw new IllegalArgumentException("node does not exist in the ring");
        }
        Set<String> shardList = nodeToShardListMap.get(node);

        for (String shardId : shardList) {
            shardToNodeMap.remove(shardId);
        }
        nodeToShardListMap.remove(node);
    }

    public void rebalanceShards(Node fromNode, Node toNode) throws IllegalArgumentException {
        if (fromNode == null || toNode == null) {
            throw new IllegalArgumentException("fromNode and toNode cannot be null");
        }
        if (!nodeToShardListMap.containsKey(fromNode)) {
            throw new IllegalArgumentException("fromNode does not exist in the ring");
        }
        if (!physicalNodeToVnodesMap.containsKey(toNode)) {
            throw new IllegalArgumentException("toNode does not exist in the ring");
        }

        Set<String> shardList = nodeToShardListMap.get(fromNode);
        if (shardList != null) {
            nodeToShardListMap.put(toNode, new HashSet<>(shardList));

            for (String shardId : shardList) {
                shardToNodeMap.put(shardId, toNode);
            }
        }
    }

    public @Nullable Set<Node> getAllNodes() {
        return physicalNodeToVnodesMap.keySet();
    }

    public @Nullable List<Long> getVNodesForNode(Node node) throws IllegalArgumentException {
        if (node == null) {
            throw new IllegalArgumentException("node cannot be null");
        }

        return physicalNodeToVnodesMap.get(node);
    }

    public int getNumPhysicalNodes() {
        return physicalNodeToVnodesMap.size();
    }

    public int getNumVirtualNodes() {
        return vnodeToPhysicalNodeMap.size();
    }
}
