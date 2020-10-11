/*
 * Created on Fri Oct 02 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.event;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sh.pancake.server.ExtraBasedMap;
import sh.pancake.server.IPancakeExtra;

/*
 *
 * Handle listeners
 * 
 * E = Event base class
 * L = Listener base class
 * 
 */
public class EventManager<E, L> {

    private static final Logger LOGGER = LogManager.getLogger("EventManager");

    private ExtraBasedMap<Class<? extends E>, EventInvoker<? extends E>> extraMap;

    public EventManager() {
        this.extraMap = new ExtraBasedMap<>();
    }

    public <R extends E>EventInvoker<R> getInvokerOf(IPancakeExtra extra, Class<R> clazz) {
        return (EventInvoker<R>) extraMap.computeIfAbsentOf(extra, clazz, key -> new EventInvoker<R>());
    }

    public <R extends E>void register(IPancakeExtra extra, Class<R> cl, IEventListenerFunc<R> func, EventPriority priority) {
        getInvokerOf(extra, cl).register(func, priority);
    }

    public <R extends E>void register(IPancakeExtra extra, Class<R> cl, IEventListenerFunc<R> func) {
        register(extra, cl, func, EventPriority.NORMAL);
    }

    public void registerAll(IPancakeExtra extra, L listener) {
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

            Class<? extends E> cl = (Class<? extends E>) method.getParameterTypes()[0];

            register(extra, cl, (event) -> {
                try {
                    method.invoke(listener, event);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, handler.priority());
        }
    }

    public <R extends E>void unregister(IPancakeExtra extra, Class<R> cl, IEventListenerFunc<R> func, EventPriority priority) {
        getInvokerOf(extra, cl).unregister(func, priority);
    }

    public <R extends E>void unregister(IPancakeExtra extra, Class<R> cl, IEventListenerFunc<R> func) {
        unregister(extra, cl, func, EventPriority.NORMAL);
    }

    public void unregisterAll(IPancakeExtra extra, L listener) {
        Method[] methodList = listener.getClass().getDeclaredMethods();
        
        for (Method method : methodList) {
            if (!method.isAnnotationPresent(EventHandler.class) ||
            !method.getReturnType().equals(Void.TYPE) ||
            method.getParameterCount() != 1) continue;

            EventHandler handler = method.getAnnotation(EventHandler.class);

            method.setAccessible(true);

            Class<? extends E> cl = (Class<? extends E>) method.getParameterTypes()[0];

            unregister(extra, cl, (event) -> {
                try {
                    method.invoke(listener, event);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, handler.priority());
        }
    }

    public void unregisterAll(IPancakeExtra extra) {
        extraMap.removeMapOf(extra);
    }

    public <R extends E>void callEvent(R event) {
        Iterator<IPancakeExtra> extraIter = extraMap.keySetMap().iterator();

        while (extraIter.hasNext()) {
            IPancakeExtra extra = extraIter.next();

            EventInvoker<R> cl = (EventInvoker<R>) getInvokerOf(extra, (Class<? extends E>) event.getClass());
            cl.invoke(event);
        }
    }

}
