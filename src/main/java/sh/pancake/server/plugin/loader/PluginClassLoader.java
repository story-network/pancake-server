/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.plugin.loader;

import java.net.URL;
import java.net.URLClassLoader;

import sh.pancake.classloader.CompositeClassLoader;
import sh.pancake.classloader.IClassLoaderProvider;

public class PluginClassLoader extends CompositeClassLoader {

    static {
        registerAsParallelCapable();
    }
    
    public PluginClassLoader(URL url, ClassLoader parent, IClassLoaderProvider provider) {
        super(new URLClassLoader(new URL[] { url }, parent), provider);
    }

}
