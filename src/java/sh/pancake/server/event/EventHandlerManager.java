/*
 * Created on Wed Aug 11 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.event;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Helper class for registering @EventHandler methods
 */
public class EventHandlerManager {

    private final EventManager manager;

    private final Map<Object, List<FixedListener<?>>> listeners;

    public EventHandlerManager(EventManager manager) {
        this.manager = manager;
        this.listeners = new WeakHashMap<>();
    }

    private List<FixedListener<?>> getEntry(Object handler) {
        return listeners.computeIfAbsent(handler, (key) -> new ArrayList<>());
    }

    public boolean isRegistered(Object handler) {
        return listeners.containsKey(handler);
    }

    /**
     * Register Event handler's listeners
     *
     * @param handler Event handler class to register
     * @return Registered listener count
     */
    @SuppressWarnings("unchecked")
    public int registerHandlers(Object handler) throws HandlerRegisterException {
        if (listeners.containsKey(handler)) return 0;

        List<FixedListener<?>> entry = new ArrayList<>();

        for (var method : handler.getClass().getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers())) continue;

            EventHandler handlerAttr = method.getDeclaredAnnotation(EventHandler.class);
            if (handlerAttr == null) continue;

            if (method.getParameterCount() != 1) {
                throw new HandlerRegisterException("EventHandler method " + method.getName() + " accepts more than one argument");
            }

            method.setAccessible(true);
            FixedListener<Object> listener = new FixedListener<>(
                handlerAttr.priority(),
                (event) -> {
                    try {
                        method.invoke(handler, event);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            );
            
            entry.add(listener);
            manager.register((Class<Object>) method.getParameterTypes()[0], listener);
        }

        getEntry(handler).addAll(entry);

        return entry.size();
    }

    public boolean unregisterAll(Object handler) {
        if (!listeners.containsKey(handler)) return false;

        getEntry(handler).clear();
        return true;
    }
    
}
