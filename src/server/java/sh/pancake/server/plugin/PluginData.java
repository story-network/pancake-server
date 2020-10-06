/*
 * Created on Fri Oct 02 2020
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.server.plugin;

import sh.pancake.server.PancakeServer;
import sh.pancake.server.plugin.loader.PluginClassLoader;

public class PluginData {

    private PancakeServer server;

    private IPancakePlugin plugin;
    private PluginInfo info;

    private PluginDataStorage dataStorage;

    private PluginClassLoader pluginClassLoader;

    public PluginData(PancakeServer server, IPancakePlugin plugin, PluginInfo info, PluginDataStorage dataStorage, PluginClassLoader pluginClassLoader) {
        this.server = server;
        this.plugin = plugin;
        this.info = info;
        this.dataStorage = dataStorage;
        this.pluginClassLoader = pluginClassLoader;
    }
    
    public PancakeServer getServer() {
        return server;
    }

    public IPancakePlugin getPlugin() {
        return plugin;
    }

    public PluginInfo getInfo() {
        return info;
    }
    
    public PluginDataStorage getDataStorage() {
        return dataStorage;
    }

    public PluginClassLoader getPluginClassLoader() {
        return pluginClassLoader;
    }

}
