/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.plugin.loader;

import java.net.URL;

import sh.pancake.classloader.ClassLoaderProvider;
import sh.pancake.classloader.CompositeURLClassLoader;

public class PluginClassLoader extends CompositeURLClassLoader {

    static {
        registerAsParallelCapable();
    }
    
    public PluginClassLoader(URL url, ClassLoaderProvider provider) {
        super(new URL[] { url }, provider);
    }

}
