/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.plugin;

public interface IPancakePlugin {

    /*
     * Called right after initialized
     * 
     * Plugin should store PluginData or you will never get it again!!
     * 
     */
    void init(PluginData data);
    
    /*
     * Invoked after loaded by PluginManager
     */
    default void onLoad() {

    }

    /*
     * Invoked before get unloaded by PluginManager
     */
    default void onUnload() {

    }

    /*
     * Invoked after server initialized
     */
    default void onServerPreInit() {

    }

    /*
     * Invoked after server successfully started
     * It called after a log like "Done (6.755s)!"
     */
    default void onServerPostInit() {

    }

}
