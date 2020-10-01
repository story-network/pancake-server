/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.plugin.loader;

import java.net.URL;
import java.net.URLClassLoader;

import sh.pancake.classloader.CompositeClassLoader;

public class PluginClassLoader extends URLClassLoader {

    static {
        registerAsParallelCapable();
    }
    
    public PluginClassLoader(URL url, CompositeClassLoader<PluginClassLoader> root) {
        super(new URL[] { url }, root);
    }

}
