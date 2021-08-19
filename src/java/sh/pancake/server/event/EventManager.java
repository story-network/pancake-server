/*
 * Created on Tue Aug 10 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.event;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * Store Event listeners and invoke them.
 */
public class EventManager implements EventDispatcher {
    
    private final Map<Class<?>, SortedSet<EventListener<Object>>> map;

    public EventManager() {
        this.map = new WeakHashMap<>();
    }

    /**
     * Dispatch event and return listener count
     *
     * @param event Event to dispatch
     */
    public void dispatchEvent(Object event) {
        var iterator = getClassEntry(event.getClass()).iterator();
        while (iterator.hasNext()) {
            iterator.next().on(event);
        }
    }

    /**
     * Register event listener
     *
     * @param <E> Event class signature
     * @param eventClass Event class
     * @param listener Event Listener
     */
    @SuppressWarnings("unchecked")
    public <E extends Object> void register(Class<? super E> eventClass, EventListener<E> listener) {
        getClassEntry(eventClass).add((EventListener<Object>) listener);
    }

    /**
     * Register predicated listener
     *
     * @param <E> Event class signature
     * @param eventClass Event class
     * @param listener Event Listener
     * @param predicate Predicate to test. Returning true unregister listener
     * @return Predicated listener constructed from listener, predicate
     */
    public <E extends Object> EventListener<E> registerPredicated(Class<? super E> eventClass, EventListener<E> listener, Function<E, Boolean> predicate) {
        EventListener<E> predicateListener = new EventListener<>() {
            @Override
            public int getPriority() {
                return listener.getPriority();
            }

            @Override
            public void on(E event) {
                if (predicate.apply(event)) {
                    unregister(eventClass, this);
                }

                listener.on(event);
            }
        };

        register(eventClass, predicateListener);

        return predicateListener;
    }

    /**
     * Register event listener. Remove itself when dispatched once.
     *
     * @param <E> Event class signature
     * @param eventClass Event class
     * @param listener Event Listener
     */
    public <E extends Object> void registerOnce(Class<? super E> eventClass, EventListener<E> listener) {
        registerPredicated(eventClass, listener, (event) -> true);
    }

    /**
     * Unregister event listener
     *
     * @param <E> Event class signature
     * @param eventClass Event class
     * @param listener Evnet Listener
     */
    public <E extends Object> boolean unregister(Class<? super E> eventClass, EventListener<E> listener) {
        return getClassEntry(eventClass).remove(listener);
    }

    /**
     * Unregister all specific event listeners
     * 
     * @param eventClass
     * @return listener count unregistered
     */
    public int unregisterAll(Class<?> eventClass) {
        SortedSet<EventListener<Object>> entry = map.get(eventClass);

        if (entry == null) return 0;

        int count = entry.size();

        entry.clear();

        return count;
    }

    /**
     * Unregister all event listeners
     * 
     * @param eventClass
     */
    public void unregisterAll() {
        map.clear();
    }

    private SortedSet<EventListener<Object>> getClassEntry(Class<?> eventClass) {
        return map.computeIfAbsent(eventClass, (key) -> new TreeSet<>());
    }

    /**
     * Count listener of event class
     * 
     * @param eventClass Event class
     * @return Listener count
     */
    public int countListeners(Class<?> eventClass) {
        return getClassEntry(eventClass).size();
    }
}
