/*
 * Created on Sun Oct 11 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class HolderBasedMap<H, K, V> {

    private WeakHashMap<H, Map<K, V>> map;

    public HolderBasedMap() {
        this.map = new WeakHashMap<>();
    }

    protected Map<K, V> createMapFor(H holder) {
        return new ConcurrentHashMap<>();
    }

    public Map<K, V> getMapOf(H holder) {
        return map.computeIfAbsent(holder, this::createMapFor);
    }

    public Map<K, V> removeMapOf(H holder) {
        return map.remove(holder);
    }

    public Set<H> keySetMap() {
        return map.keySet();
    }

    public Collection<Map<K, V>> valuesMap() {
        return map.values();
    }

    public int sizeOf(H holder) {
        return getMapOf(holder).size();
    }



    public V getOf(H holder, K key) {
        return getMapOf(holder).get(key);
    }

    public V getOrDefaultOf(H holder, K key, V defaultValue) {
        return getMapOf(holder).getOrDefault(key, defaultValue);
    }

    public void putOf(H holder, K key, V value) {
        getMapOf(holder).put(key, value);
    }

    public void putAllOf(H holder, Map<? extends K, ? extends V> target) {
        getMapOf(holder).putAll(target);
    }

    public V removeOf(H holder, K key) {
        return getMapOf(holder).remove(key);
    }



    public boolean containsKeyOf(H holder, K key) {
        return getMapOf(holder).containsKey(key);
    }

    public boolean containsValueOf(H holder, V value) {
        return getMapOf(holder).containsValue(value);
    }



    public void forEachOf(H holder, BiConsumer<? super K, ? super V> action) {
        getMapOf(holder).forEach(action);
    }

    public V computeOf(H holder, K key, BiFunction<? super K, ? super V, ? extends V> action) {
        return getMapOf(holder).compute(key, action);
    }

    public V computeIfAbsentOf(H holder, K key, Function<? super K, ? extends V> action) {
        return getMapOf(holder).computeIfAbsent(key, action);
    }

    public V computeIfPresentOf(H holder, K key, BiFunction<? super K, ? super V, ? extends V> action) {
        return getMapOf(holder).computeIfPresent(key, action);
    }





    public Set<K> keysOf(H holder) {
        return getMapOf(holder).keySet();
    }

    public Collection<V> valuesOf(H holder) {
        return getMapOf(holder).values();
    }

    public Set<Map.Entry<K, V>> entrySetOf(H holder) {
        return getMapOf(holder).entrySet();
    }



    public void clearOf(H holder) {
        getMapOf(holder).clear();
    }

}
