/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.launcher.classloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Iterator;

public class CompositeURLClassLoader extends URLClassLoader {
    
    static {
        registerAsParallelCapable();
    }

    private ClassLoaderProvider provider;

    public CompositeURLClassLoader(URL[] urls, ClassLoaderProvider provider) {
        // ClassLoaderProvider should provide parent classes too
        super(urls, null);
        this.provider = provider;

        provider.addSubLoader(this);
    }

    @Override
    public URL findResource(String name) {
        URL url = super.findResource(name);

        if (url != null) return url;

        Iterator<ClassLoader> iter = provider.getLoaderIterator();
        while (iter.hasNext()) {
            ClassLoader loader = iter.next();
            if (loader != this && (url = loader.getResource(name)) != null) return url;
        }

        return null;
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        Enumeration<URL> main = null;

        try {
            main = super.findResources(name);
        } catch (IOException e) {
            
        }

        Iterator<ClassLoader> iter = provider.getLoaderIterator();

        Enumeration<URL> current = main;
        return new Enumeration<URL>() {

            private Enumeration<URL> currentEnumeration = current;

            @Override
            public boolean hasMoreElements() {
                try {
                    if (currentEnumeration == null) {
                        ClassLoader loader = iter.next();
                        if (loader == CompositeURLClassLoader.this) loader = iter.next();

                        // Just skip more duplication
                        currentEnumeration = loader.getResources(name);
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
                        ClassLoader loader = iter.next();
                        if (loader == CompositeURLClassLoader.this) loader = iter.next();

                        // Just skip more duplication
                        currentEnumeration = loader.getResources(name);
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
            return super.findClass(name);
        } catch (ClassNotFoundException e) {

        }

        Iterator<ClassLoader> iter = provider.getLoaderIterator();
        while (iter.hasNext()) {
            ClassLoader loader = iter.next();

            if (loader == this) continue;

            try {
                return Class.forName(name, true, loader);
            } catch (Exception e) {

            }
        }

        throw new ClassNotFoundException(name + " is undefined");
    }

    @Override
    public void close() throws IOException {
        super.close();

        provider.removeSubLoader(this);
        provider = null;
    }

}
