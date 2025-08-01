package com.github.sbcharr.consistenthashing;

/**
 * Interface to compute the hash of a given key in a specified hash space.
 */
public interface Hashable {
    /**
     * Hashes the given key into a value within the given hash space.
     *
     * @param key the input string (e.g., node or shard ID)
     * @param hashSpace the size of the hash ring (e.g., 2^32 or 2^64)
     * @return hash value in range [0, hashSpace)
     */
    long hash(String key, long hashSpace);
}