package com.github.sbcharr.consistenthashing;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class MurmurHash3Hashable implements Hashable {

    @Override
    public long hash(String key, long hashSpace) {
        // Hashes the key using MurmurHash3 (128-bit), returns lower 64-bit unsigned value within [0, hashSpace)
        long unsignedHash = Hashing.murmur3_128().hashString(key, StandardCharsets.UTF_8).asLong() & Long.MAX_VALUE;
        return unsignedHash % hashSpace;
    }

}
