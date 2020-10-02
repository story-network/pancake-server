/*
 * Created on Fri Oct 02 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mod;

import sh.pancake.server.PancakeServer;

public class ModData {

    private PancakeServer server;

    private IPancakeMod mod;
    private ModInfo info;
    
    private ModDataStorage dataStorage;
    
    public ModData(PancakeServer server, IPancakeMod mod, ModInfo info, ModDataStorage dataStorage) {
        this.server = server;
        this.mod = mod;
        this.info = info;
        this.dataStorage = dataStorage;
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

}
