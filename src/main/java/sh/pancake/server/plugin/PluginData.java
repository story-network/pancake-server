/*
 * Created on Fri Oct 02 2020
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.server.plugin;

import sh.pancake.server.plugin.loader.PluginClassLoader;

public class PluginData {

    private IPancakePlugin plugin;
    private PluginInfo info;

    private PluginClassLoader pluginClassLoader;

    public PluginData(IPancakePlugin plugin, PluginInfo info, PluginClassLoader pluginClassLoader) {
        this.plugin = plugin;
        this.info = info;
        this.pluginClassLoader = pluginClassLoader;
    }

    public IPancakePlugin getPlugin() {
        return plugin;
    }

    public PluginInfo getInfo() {
        return info;
    }

    public PluginClassLoader getPluginClassLoader() {
        return pluginClassLoader;
    }

}
