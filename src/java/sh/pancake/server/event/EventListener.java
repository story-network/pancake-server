/*
 * Created on Wed Aug 11 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.event;

/**
 * Event listener with priority and listener method.
 * The default priority = 0. High priority listener get called earlier.
 */
@FunctionalInterface
public interface EventListener<T> extends Comparable<EventListener<?>> {

    void on(T event);
    
    default int getPriority() {
        return 0;
    }

    @Override
    default int compareTo(EventListener<?> other) {
        // Higher priority listener should come first, so reversing order.
        return other.getPriority() - getPriority();
    }

}
