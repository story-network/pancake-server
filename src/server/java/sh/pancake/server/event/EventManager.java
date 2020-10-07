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

/*
 *
 * Handle entire event listeners
 * 
 */
public class EventManager<T> {

    private static final Logger LOGGER = LogManager.getLogger("EventManager");

    // Separate EventMap by IPancakeExtra so plugin events can unload when they are unloaded
    private WeakHashMap<T, EventMap> extraMap;

    public EventManager() {
        this.extraMap = new WeakHashMap<>();
    }

    protected EventMap getEventMap(T extra) {
        return extraMap.computeIfAbsent(extra, (ex) -> new EventMap());
    }

    public <R extends IEvent>void register(T extra, Class<R> cl, IEventListenerFunc<R> func, EventPriority priority) {
        getEventMap(extra).register(cl, func, priority);
    }

    public <R extends IEvent>void register(T extra, Class<R> cl, IEventListenerFunc<R> func) {
        register(extra, cl, func, EventPriority.NORMAL);
    }

    public void registerAll(T extra, IEventListener listener) {
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

    public <R extends IEvent>void unregister(T extra, Class<R> cl, IEventListenerFunc<R> func, EventPriority priority) {
        getEventMap(extra).unregister(cl, func, priority);
    }

    public <R extends IEvent>void unregister(T extra, Class<R> cl, IEventListenerFunc<R> func) {
        unregister(extra, cl, func, EventPriority.NORMAL);
    }

    public void unregisterAll(T extra, IEventListener listener) {
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

    public void unregisterAll(T extra) {
        extraMap.remove(extra);
    }

    public <R extends IEvent>void callEvent(R event) {
        Iterator<EventMap> eventMapIter = extraMap.values().iterator();

        while (eventMapIter.hasNext()) {
            EventInvoker<R> cl = (EventInvoker<R>) eventMapIter.next().getInvokerFor(event.getClass());
            cl.invoke(event);
        }
    }

    protected class EventMap {
        
        private Map<Class<?>, EventInvoker<?>> eventMap;

        public EventMap() {
            this.eventMap = new ConcurrentHashMap<>();
        }

        public <R extends IEvent>EventInvoker<R> getInvokerFor(Class<? extends R> clazz) {
            return (EventInvoker<R>) eventMap.computeIfAbsent(clazz, key -> new EventInvoker<R>());
        }

        public <R extends IEvent>void register(Class<R> cl, IEventListenerFunc<R> func, EventPriority priority) {
            EventInvoker<R> invoker = getInvokerFor(cl);
            invoker.register(func, priority);
        }

        public <R extends IEvent>void unregister(Class<R> cl, IEventListenerFunc<R> func, EventPriority priority) {
            EventInvoker<R> invoker = getInvokerFor(cl);
            invoker.unregister(func, priority);
        }

    }

}
