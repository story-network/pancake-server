/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.classloader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class CompositeClassLoader<T extends ClassLoader> extends ClassLoader {
    
    static {
        registerAsParallelCapable();
    }

    private List<T> subLoaderList;

    public CompositeClassLoader(ClassLoader parent) {
        super(parent);
        this.subLoaderList = new ArrayList<>();
    }

    public boolean isAdded(T loader) {
        return subLoaderList.contains(loader);
    }

    public boolean addSubLoader(T loader) {
        if (isAdded(loader)) return false;

        return subLoaderList.add(loader);
    }

    public boolean removeSubLoader(T loader) {
        return subLoaderList.remove(loader);
    }
    
    @Override
    protected URL findResource(String name) {
        URL url = null;

        for (ClassLoader loader : subLoaderList) {
            if ((url = loader.getResource(name)) != null) return url;
        }

        return null;
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        if (subLoaderList.size() < 1) throw new IOException();

        return new Enumeration<URL>() {

            private int i = 0;
            private Enumeration<URL> currentEnumeration = null;

            @Override
            public boolean hasMoreElements() {
                try {
                    if (currentEnumeration == null) {
                        currentEnumeration = subLoaderList.get(i).getResources(name);
                    }
                
                    return currentEnumeration.hasMoreElements();
                } catch (Exception e) {
                    if (++i < subLoaderList.size()) {
                        currentEnumeration = null;
                        return hasMoreElements();
                    }
                }

                return false;
            }

            @Override
            public URL nextElement() {
                try {
                    if (currentEnumeration == null) {
                        currentEnumeration = subLoaderList.get(i).getResources(name);
                    }
                
                    return currentEnumeration.nextElement();
                } catch (Exception e) {
                    if (++i < subLoaderList.size()) {
                        currentEnumeration = null;
                        return nextElement();
                    }
                }

                return null;
            }
            
        };
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> c = null;

        for (ClassLoader loader : subLoaderList) {
            if ((c = loader.loadClass(name)) != null) return c;
        }

        throw new ClassNotFoundException(name + " is undefined");
    }

}
