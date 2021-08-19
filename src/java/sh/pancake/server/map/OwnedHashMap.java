/*
 * Created on Tue Aug 10 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.map;

import java.util.HashMap;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Stream;

import javax.annotation.Nullable;

/**
 * Owned HashMap stores key, value in sub map of owned Object.
 * The owned Object is weak reference. When the owned Object gc, rest of sub entries will be deleted.
 * Preventing possible memory leak.
 */
public class OwnedHashMap<O, K, V> {

    private final WeakHashMap<O, HashMap<K, V>> rootMap;
    
    public OwnedHashMap() {
        this.rootMap = new WeakHashMap<>();
    }

    private HashMap<K, V> getEntryInner(O owned) {
        return rootMap.computeIfAbsent(owned, (key) -> new HashMap<>());
    }
    
    /**
     * Get sub map of owned object. The Returned map is guarded.
     *
     * @param owned
     * @return
     */
    public GuardedMap<K, V> getEntry(O owned) {
        return new GuardedMap<K, V>(getEntryInner(owned));
    }

    /**
     * Remove(clear) entry from root map
     * 
     * @param owned
     * @return Inner map. If existed.
     */
    @Nullable
    public HashMap<K, V> removeEntry(O owned) {
        return rootMap.remove(owned);
    }

    /**
     * Get values from every sub map.
     *
     * @param key
     * @return
     */
    public Stream<V> getAllStream(K key) {
        return rootMap.values().stream().map((map) -> map.get(key)).filter(Objects::nonNull);
    }

    /**
     * Iterate every sub maps
     * @return
     */
    public Stream<GuardedMap<K, V>> valuesStream() {
        return rootMap.values().stream().map((value) -> new GuardedMap<>(value));
    }

}
