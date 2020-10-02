/*
 * Created on Fri Oct 02 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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
public class EventManager {

    private static final Logger LOGGER = LogManager.getLogger("EventManager");

    // Separate EventMap by classloader so plugin events can unload when they are unloaded
    private WeakHashMap<ClassLoader, EventMap> classLoaderMap;

    public EventManager() {
        this.classLoaderMap = new WeakHashMap<>();
    }

    protected EventMap getEventMap(ClassLoader classLoader) {
        return classLoaderMap.computeIfAbsent(classLoader, key -> new EventMap());
    }

    protected EventMap getEventMapFor(Object caller) {
        return getEventMap(caller.getClass().getClassLoader());
    }

    public <T>void register(Class<T> cl, IEventListenerFunc<T> func, EventPriority priority) {
        getEventMapFor(func).register(cl, func, priority);
    }

    public <T>void register(Class<T> cl, IEventListenerFunc<T> func) {
        register(cl, func, EventPriority.NORMAL);
    }

    public void registerAll(IEventListener listener) {
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

            LOGGER.info(method.getParameterTypes()[0]);

            register(method.getParameterTypes()[0], (event) -> {
                try {
                    method.invoke(listener, event);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, handler.priority());
        }
    }

    public <T>void unregister(Class<T> cl, IEventListenerFunc<T> func, EventPriority priority) {
        getEventMapFor(func).unregister(cl, func, priority);
    }

    public <T>void unregister(Class<T> cl, IEventListenerFunc<T> func) {
        unregister(cl, func, EventPriority.NORMAL);
    }

    public void unregisterAll(IEventListener listener) {
        Method[] methodList = listener.getClass().getDeclaredMethods();
        
        for (Method method : methodList) {
            if (!method.isAnnotationPresent(EventHandler.class) ||
            !method.getReturnType().equals(Void.TYPE) ||
            method.getParameterCount() != 1) continue;

            EventHandler handler = method.getAnnotation(EventHandler.class);

            method.setAccessible(true);

            unregister(method.getParameterTypes()[0], (event) -> {
                try {
                    method.invoke(listener, event);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, handler.priority());
        }
    }

    public <T>void callEvent(T event) {
        Iterator<EventMap> eventMapIter = classLoaderMap.values().iterator();

        while (eventMapIter.hasNext()) {
            eventMapIter.next().getInvokerFor((Class<T>) event.getClass()).invoke(event);
        }
    }

    protected class EventMap {
        
        private Map<Class<?>, EventInvoker<?>> eventMap;

        public EventMap() {
            this.eventMap = new ConcurrentHashMap<>();
        }

        public <T>EventInvoker<T> getInvokerFor(Class<T> clazz) {
            return (EventInvoker<T>) eventMap.computeIfAbsent(clazz, key -> new EventInvoker<T>());
        }

        public <T>void register(Class<T> cl, IEventListenerFunc<T> func, EventPriority priority) {
            EventInvoker<T> invoker = getInvokerFor(cl);
            invoker.register(func, priority);
        }

        public <T>void unregister(Class<T> cl, IEventListenerFunc<T> func, EventPriority priority) {
            EventInvoker<T> invoker = getInvokerFor(cl);
            invoker.unregister(func, priority);
        }

    }

}
