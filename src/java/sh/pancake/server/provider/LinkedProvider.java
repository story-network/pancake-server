/*
 * Created on Sun Aug 22 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.provider;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * Provide linked object.
 * 
 * Provider focus on providing linked object without caring state of key object like weak map.
 * Prefer using composed object rather than pritmitive object for this implementation.
 */
public class LinkedProvider<K, V> implements DependentProvider<K, V> {

    private final Function<K, V> allocator;

    private final Map<K, V> map;

    /**
     * Consturct new LinkedProvider.
     * 
     * Allocator function must not store key object, excluding any identifier value required to work.
     * @param allocator
     */
    public LinkedProvider(Function<K, V> allocator) {
        this.allocator = allocator;
        this.map = new WeakHashMap<>();
    }

    @Override
    public V get(K key) {
        return map.computeIfAbsent(key, allocator);
    }

}
