/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mod.loader;

import java.net.URL;
import java.net.URLClassLoader;

import sh.pancake.classloader.CompositeClassLoader;

public class ModClassLoader extends URLClassLoader {

    public ModClassLoader(String modId, URL url, CompositeClassLoader<ModClassLoader> root) {
        super(modId, new URL[] { url }, root);
    }
    
}
