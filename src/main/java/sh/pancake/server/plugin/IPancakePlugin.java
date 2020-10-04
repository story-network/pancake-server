/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.plugin;

import sh.pancake.server.IPancakeExtra;

public interface IPancakePlugin extends IPancakeExtra {

    /*
     * Called right after initialized
     * 
     * Plugin should store PluginData or you will never get it again!!
     * 
     */
    void init(PluginData data);

}
