/*
 * Created on Tue Aug 10 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.map;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * GuardedMap holds Map as Weak Reference interally.
 * Prevents user abusing inner map from being gc.
 * 
 * When referenced map gc. The map operates like Collections.emptyMap()
 */
public class GuardedMap<K, V> implements Map<K, V> {

    private final WeakReference<Map<K, V>> ref;

    public GuardedMap(Map<K, V> map) {
        this.ref = new WeakReference<>(map);
    }

    /**
     * Check if reference is alive
     *
     * @return true if reference is alive
     */
    public boolean hasRef() {
        return ref.get() != null;
    }

    private Map<K, V> getRef() {
        Map<K, V> map = ref.get();
        if (map != null) return map;

        return Collections.emptyMap();
    }

    @Override
    public int size() {
        return getRef().size();
    }

    @Override
    public boolean isEmpty() {
        return getRef().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return getRef().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getRef().containsValue(value);
    }

    @Override
    public V get(Object key) {
        return getRef().get(key);
    }

    @Override
    public V put(K key, V value) {
        return getRef().put(key, value);
    }

    @Override
    public V remove(Object key) {
        return getRef().remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        getRef().putAll(m);
    }

    @Override
    public void clear() {
        getRef().clear();
    }

    @Override
    public Set<K> keySet() {
        return getRef().keySet();
    }

    @Override
    public Collection<V> values() {
        return getRef().values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return getRef().entrySet();
    }
}
