/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server;

import sh.pancake.classloader.ModdedClassLoader;
import sh.pancake.common.storage.ObjectStorage;

public interface IPancakeServer {

     // VERSION TO DOWNLOAD
     String getVersion();
 
     // Impl must call finishMixinInit after finshing mixin configuration
     void start(String[] args, ModdedClassLoader loader, ObjectStorage serverDataStorage, Runnable finishMixinInit);

}
