/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.launcher;

import sh.pancake.launcher.classloader.ServerClassLoader;

public interface IPancakeServer {

     String getVersion();
 
     // Impl must call finishMixinInit after finshing mixin configuration
     void start(String[] args, ServerClassLoader classLoader, Runnable finishMixinInit);

}
