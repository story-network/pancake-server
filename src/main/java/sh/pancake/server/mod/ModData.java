/*
 * Created on Fri Oct 02 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mod;

import sh.pancake.server.PancakeServer;
import sh.pancake.server.mod.loader.ModClassLoader;

public class ModData {

    private PancakeServer server;

    private IPancakeMod mod;
    private ModInfo info;
    
    private ModDataStorage dataStorage;

    private ModClassLoader modClassLoader;
    
    public ModData(PancakeServer server, IPancakeMod mod, ModInfo info, ModDataStorage dataStorage, ModClassLoader modClassLoader) {
        this.server = server;
        this.mod = mod;
        this.info = info;
        this.dataStorage = dataStorage;
        this.modClassLoader = modClassLoader;
    }

    public PancakeServer getServer() {
        return server;
    }

    public IPancakeMod getMod() {
        return mod;
    }

    public ModInfo getInfo() {
        return info;
    }

    public ModDataStorage getDataStorage() {
        return dataStorage;
    }

    public ModClassLoader getModClassLoader() {
        return modClassLoader;
    }

}
