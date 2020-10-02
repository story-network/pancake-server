/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mod.loader;

import java.net.URL;
import java.net.URLClassLoader;

public class ModClassLoader extends URLClassLoader {

    static {
        registerAsParallelCapable();
    }
    
    public ModClassLoader(URL url) {
        // Parent classloader presents using ClassLoaderProvider
        super(new URL[] { url }, null);
    }

}
