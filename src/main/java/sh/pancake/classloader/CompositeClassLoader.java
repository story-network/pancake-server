/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.classloader;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

public class CompositeClassLoader extends ClassLoader {
    
    static {
        registerAsParallelCapable();
    }

    private ClassLoader classLoader;
    private IClassLoaderProvider provider;

    public CompositeClassLoader(ClassLoader classLoader, IClassLoaderProvider provider) {
        this.classLoader = classLoader;
        this.provider = provider;
    }

    public IClassLoaderProvider getProvider() {
        return provider;
    }
    
    @Override
    protected URL findResource(String name) {
        URL url = classLoader.getResource(name);

        if (url != null) return url;

        Iterator<ClassLoader> iter = provider.getLoaderIterator();
        while (iter.hasNext()) {
            if ((url = iter.next().getResource(name)) != null) return url;
        }

        return null;
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        Enumeration<URL> main = null;
        try {
            main = classLoader.getResources(name);
        } catch (Exception e) {
            
        }

        Iterator<ClassLoader> iter = provider.getLoaderIterator();

        Enumeration<URL> current = main;
        return new Enumeration<URL>() {

            private Enumeration<URL> currentEnumeration = current;

            @Override
            public boolean hasMoreElements() {
                try {
                    if (currentEnumeration == null) {
                        currentEnumeration = iter.next().getResources(name);
                    }
                
                    return currentEnumeration.hasMoreElements();
                } catch (Exception e) {
                    if (iter.hasNext()) {
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
                        currentEnumeration = iter.next().getResources(name);
                    }
                
                    return currentEnumeration.nextElement();
                } catch (Exception e) {
                    if (iter.hasNext()) {
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
        try {
            return classLoader.loadClass(name);
        } catch (Exception e) {

        }

        Class<?> c = null;

        Iterator<ClassLoader> iter = provider.getLoaderIterator();
        while (iter.hasNext()) {
            if ((c = iter.next().loadClass(name)) != null) return c;
        }

        throw new ClassNotFoundException(name + " is undefined");
    }

}
