/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mod;

public interface IPancakeMod {

    /*
     * Called right after initialized
     * 
     * Mod should store ModData or you will never get it again!!
     * 
     */
    void init(ModData data);

    /*
     * Invoked after loaded by ModManager
     */
    default void onLoad() {

    }

    /*
     * Invoked before get unloaded by ModManager
     */
    default void onUnload() {

    }

    /*
     * Invoked after server initialized from PancakeServer
     */
    default void onServerInitialized() {

    }
    
}
