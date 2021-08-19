/*
 * Created on Thu Aug 19 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.plugin;

import sh.pancake.server.PancakeServer;
import sh.pancake.server.extension.Extension;

/**
 * Entrypoint for plugin.
 * Plugin main class must implement this.
 */
public interface PluginEntrypoint {

    void pluginMain(PancakeServer server, Extension<PluginInfo> extension);
    
}
