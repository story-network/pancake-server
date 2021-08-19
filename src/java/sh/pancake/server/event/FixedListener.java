/*
 * Created on Wed Aug 11 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.event;

/**
 * Event listener with customizable priority
 */
public class FixedListener<T> implements EventListener<T> {

    private final int priority;
    private final EventListener<? super T> listener;

    public FixedListener(int priority, EventListener<? super T> listener) {
        this.priority = priority;
        this.listener = listener;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void on(T event) {
        listener.on(event);
    }

}
