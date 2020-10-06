/*
 * Created on Mon Oct 05 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.launcher.classloader;

import sh.pancake.launcher.mod.IClassModder;

public class ServerClassLoader extends ModdedClassLoader {

    public ServerClassLoader(ClassLoader classLoader, IClassModder modder) {
        super(classLoader, modder);
    }
    
}
