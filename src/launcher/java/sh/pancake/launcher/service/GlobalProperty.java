/*
 * Created on Sat Sep 26 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.launcher.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

public class GlobalProperty implements IGlobalPropertyService {

    private Map<IPropertyKey, Object> map = new ConcurrentHashMap<>();

    @Override
    public IPropertyKey resolveKey(String name) {
        return new Key(name);
    }

    @Override
    public <T> T getProperty(IPropertyKey key) {
        return (T) map.get(key);
    }

    @Override
    public void setProperty(IPropertyKey key, Object value) {
        map.put(key, value);
    }

    @Override
    public <T> T getProperty(IPropertyKey key, T defaultValue) {
        T val = this.getProperty(key);
        
        if (val == null) return defaultValue;

        return val;
    }

    @Override
    public String getPropertyString(IPropertyKey key, String defaultValue) {
        return this.<String>getProperty(key, defaultValue);
    }

    public class Key implements IPropertyKey {

        private String name;

        public Key(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }
}