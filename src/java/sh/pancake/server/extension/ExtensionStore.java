/*
 * Created on Thu Aug 12 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.extension;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * Stores extensions and namespace.
 * 
 * While this store works like Set, inner implmentation uses Map for fast id lookup.
 */
public class ExtensionStore<T> implements Set<Extension<T>> {
    
    private final String namespace;

    private final Map<String, Extension<T>> map;

    public ExtensionStore(String namespace) {
        this.namespace = namespace;

        this.map = new HashMap<>();
    }

    public String getNamespace() {
        return namespace;
    }

    @Nullable
    public Extension<T> get(String id) {
        return map.get(id);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return map.containsValue(o);
    }

    @Override
    public Iterator<Extension<T>> iterator() {
        return map.values().iterator();
    }

    @Override
    public Object[] toArray() {
        return map.values().toArray();
    }

    @Override
    public <E> E[] toArray(E[] a) {
        return map.values().toArray(a);
    }

    @Override
    public boolean add(Extension<T> e) {
        if (map.containsKey(e.getId())) return false;

        map.put(e.getId(), e);

        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof Extension) {
            return map.remove(((Extension<?>) o).getId()) != null;
        } {
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return map.values().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Extension<T>> c) {
        boolean res = false;

        for (var o : c) {
            if (add(o) && !res) {
                res = true;
            }
        }

        return res;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return map.values().retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean res = false;

        for (var o : c) {
            if (remove(o) && !res) {
                res = true;
            }
        }

        return res;
    }

    @Override
    public void clear() {
        map.clear();
    }

}
