/*
 * Created on Fri Oct 02 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class EventInvoker<T> {

    private Collection<IEventListenerFunc<T>> latestCollection;
    private Collection<IEventListenerFunc<T>> lateCollection;
    private Collection<IEventListenerFunc<T>> normalCollection;
    private Collection<IEventListenerFunc<T>> earlyCollection;
    private Collection<IEventListenerFunc<T>> earliestCollection;

    public EventInvoker() {
        this.latestCollection = new ArrayList<>();
        this.lateCollection = new ArrayList<>();
        this.normalCollection = new ArrayList<>();
        this.earlyCollection = new ArrayList<>();
        this.earliestCollection = new ArrayList<>();
    }

    public Collection<IEventListenerFunc<T>> getTargetList(EventPriority priority) {
        switch(priority) {
            case LATEST: return latestCollection;
            case LATE: return lateCollection;
            case EARLY: return earlyCollection;
            case EARLIEST: return earliestCollection;
            case NORMAL:
            default: return normalCollection;
        }
    }

    public void register(IEventListenerFunc<T> func, EventPriority priority) {
        getTargetList(priority).add(func);
    }

    public void unregister(IEventListenerFunc<T> func, EventPriority priority) {
        getTargetList(priority).remove(func);
    }

    public void invoke(T event) {
        Iterator<IEventListenerFunc<T>> earliestIter = earliestCollection.iterator();
        Iterator<IEventListenerFunc<T>> earlyIter = earlyCollection.iterator();
        Iterator<IEventListenerFunc<T>> normalIter = normalCollection.iterator();
        Iterator<IEventListenerFunc<T>> lateIter = lateCollection.iterator();
        Iterator<IEventListenerFunc<T>> latestIter = latestCollection.iterator();

        while (earliestIter.hasNext()) {
            earliestIter.next().on(event);
        }

        while (earlyIter.hasNext()) {
            earlyIter.next().on(event);
        }

        while (normalIter.hasNext()) {
            normalIter.next().on(event);
        }

        while (lateIter.hasNext()) {
            lateIter.next().on(event);
        }

        while (latestIter.hasNext()) {
            latestIter.next().on(event);
        }
    }

    public void clear() {
        latestCollection.clear();
        lateCollection.clear();
        normalCollection.clear();
        earlyCollection.clear();
        earliestCollection.clear();
    }

}