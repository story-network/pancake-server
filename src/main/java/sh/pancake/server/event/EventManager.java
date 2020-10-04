/*
 * Created on Fri Oct 02 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.event;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sh.pancake.server.IPancakeExtra;

/*
 *
 * Handle entire event listeners
 * 
 */
public class EventManager {

    private static final Logger LOGGER = LogManager.getLogger("EventManager");

    // Separate EventMap by IPancakeExtra so plugin events can unload when they are unloaded
    private WeakHashMap<IPancakeExtra, EventMap> extraMap;

    public EventManager() {
        this.extraMap = new WeakHashMap<>();
    }

    protected EventMap getEventMap(IPancakeExtra extra) {
        return extraMap.computeIfAbsent(extra, (ex) -> new EventMap());
    }

    public <R extends IEvent>void register(IPancakeExtra extra, Class<R> cl, IEventListenerFunc<R> func, EventPriority priority) {
        getEventMap(extra).register(cl, func, priority);
    }

    public <R extends IEvent>void register(IPancakeExtra extra, Class<R> cl, IEventListenerFunc<R> func) {
        register(extra, cl, func, EventPriority.NORMAL);
    }

    public void registerAll(IPancakeExtra extra, IEventListener listener) {
        Method[] methodList = listener.getClass().getDeclaredMethods();

        for (Method method : methodList) {
            EventHandler handler = method.getAnnotation(EventHandler.class);

            if (handler == null) continue;

            if (!method.getReturnType().equals(Void.TYPE)) {
                LOGGER.warn("Skipping event function " + method.getName() + " at" + listener.getClass().getName() + ", it should return void!!");
                continue;
            }

            if (method.getParameterCount() != 1) {
                LOGGER.warn("Skipping event function " + method.getName() + " at" + listener.getClass().getName() + ", parameter count is not 1 ??");
                continue;
            }

            method.setAccessible(true);

            Class<?> cl = method.getParameterTypes()[0];

            if (!Arrays.asList(cl.getInterfaces()).contains(IEvent.class)) {
                LOGGER.warn("Skipping event function " + method.getName() + " at" + listener.getClass().getName() + ", parameter should implements IEvent");
                continue;
            }

            register(extra, (Class<? extends IEvent>) cl, (event) -> {
                try {
                    method.invoke(listener, event);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, handler.priority());
        }
    }

    public <R extends IEvent>void unregister(IPancakeExtra extra, Class<R> cl, IEventListenerFunc<R> func, EventPriority priority) {
        getEventMap(extra).unregister(cl, func, priority);
    }

    public <R extends IEvent>void unregister(IPancakeExtra extra, Class<R> cl, IEventListenerFunc<R> func) {
        unregister(extra, cl, func, EventPriority.NORMAL);
    }

    public void unregisterAll(IPancakeExtra extra, IEventListener listener) {
        Method[] methodList = listener.getClass().getDeclaredMethods();
        
        for (Method method : methodList) {
            if (!method.isAnnotationPresent(EventHandler.class) ||
            !method.getReturnType().equals(Void.TYPE) ||
            method.getParameterCount() != 1) continue;

            EventHandler handler = method.getAnnotation(EventHandler.class);

            method.setAccessible(true);

            Class<?> cl = method.getParameterTypes()[0];

            if (!Arrays.asList(cl.getInterfaces()).contains(IEvent.class)) continue;

            unregister(extra, (Class<? extends IEvent>) cl, (event) -> {
                try {
                    method.invoke(listener, event);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, handler.priority());
        }
    }

    public void unregisterAll(IPancakeExtra extra) {
        extraMap.remove(extra);
    }

    public <T extends IEvent>void callEvent(T event) {
        Iterator<EventMap> eventMapIter = extraMap.values().iterator();

        while (eventMapIter.hasNext()) {
            EventInvoker<T> cl = (EventInvoker<T>) eventMapIter.next().getInvokerFor(event.getClass());
            cl.invoke(event);
        }
    }

    protected class EventMap {
        
        private Map<Class<?>, EventInvoker<?>> eventMap;

        public EventMap() {
            this.eventMap = new ConcurrentHashMap<>();
        }

        public <T extends IEvent>EventInvoker<T> getInvokerFor(Class<? extends T> clazz) {
            return (EventInvoker<T>) eventMap.computeIfAbsent(clazz, key -> new EventInvoker<T>());
        }

        public <T extends IEvent>void register(Class<T> cl, IEventListenerFunc<T> func, EventPriority priority) {
            EventInvoker<T> invoker = getInvokerFor(cl);
            invoker.register(func, priority);
        }

        public <T extends IEvent>void unregister(Class<T> cl, IEventListenerFunc<T> func, EventPriority priority) {
            EventInvoker<T> invoker = getInvokerFor(cl);
            invoker.unregister(func, priority);
        }

    }

}
