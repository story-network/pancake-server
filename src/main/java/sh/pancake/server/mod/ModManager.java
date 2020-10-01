/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sh.pancake.classloader.CompositeClassLoader;
import sh.pancake.common.storage.DiskIOStorage;
import sh.pancake.server.mod.loader.ModClassLoader;

public class ModManager {

    private static final Logger LOGGER = LogManager.getLogger("ModManager");
    
    private DiskIOStorage modStorage;

    private CompositeClassLoader<ModClassLoader> modRootClassLoader;

    public ModManager(String modFolderName, ClassLoader serverClassLoader) {
        this.modStorage = new DiskIOStorage(modFolderName);
        
        this.modRootClassLoader = new CompositeClassLoader<>(serverClassLoader);
    }

    public DiskIOStorage getModStorage() {
        return modStorage;
    }

    public CompositeClassLoader<ModClassLoader> getModRootClassLoader() {
        return modRootClassLoader;
    }

}
