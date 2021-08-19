/*
 * Created on Thu Aug 19 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mod;

import sh.pancake.server.PancakeServer;
import sh.pancake.server.extension.Extension;

/**
 * Entrypoint for mod.
 * Mod main class must implement this.
 */
public interface ModEntrypoint {

    void modMain(PancakeServer server, Extension<ModInfo> extension);
    
}
