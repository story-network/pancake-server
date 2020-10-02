/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mod;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sh.pancake.classloader.ClassLoaderProvider;
import sh.pancake.common.storage.DiskIOStorage;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.mod.loader.ModClassLoader;

public class ModManager {

    private static final Logger LOGGER = LogManager.getLogger("ModManager");

    private PancakeServer server;
    
    private DiskIOStorage modStorage;

    ClassLoaderProvider extraClassLoaderProvider;

    public ModManager(PancakeServer server, String modFolderName, ClassLoaderProvider extraClassLoaderProvider) {
        this.server = server;
        this.modStorage = new DiskIOStorage(modFolderName);
        
        this.extraClassLoaderProvider = extraClassLoaderProvider;
    }

    public PancakeServer getServer() {
        return server;
    }

    public DiskIOStorage getModStorage() {
        return modStorage;
    }

	public void loadMod(File file) {
        
	}

}
