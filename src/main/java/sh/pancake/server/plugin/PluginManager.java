/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.google.gson.Gson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sh.pancake.classloader.ClassLoaderProvider;
import sh.pancake.common.storage.DiskIOStorage;
import sh.pancake.server.Constants;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.plugin.loader.PluginClassLoader;

/*
 *
 * Manage plugins. Can be used for interacting between plugins
 * 
 */
public class PluginManager {

    private static final Logger LOGGER = LogManager.getLogger("PluginManager");

    private PancakeServer server;

    private DiskIOStorage pluginStorage;

    private ClassLoader serverClassLoader;
    private ClassLoaderProvider pluginClassLoaderProvider;

    private Map<String, PluginData> pluginMap;

    public PluginManager(PancakeServer server, String pluginFolderName, ClassLoader serverClassLoader) {
        this.server = server;
        this.pluginStorage = new DiskIOStorage(pluginFolderName);
        
        this.serverClassLoader = serverClassLoader;
        this.pluginClassLoaderProvider = new ClassLoaderProvider();

        this.pluginMap = new ConcurrentHashMap<>();
    }

    public PancakeServer getServer() {
        return server;
    }

    public DiskIOStorage getPluginStorage() {
        return pluginStorage;
    }

    public boolean hasPluginId(String pluginId) {
        return pluginMap.containsKey(pluginId);
    }

    public PluginData loadPlugin(File pluginFile) throws Exception {
        PluginInfo info = null;

        try (JarFile pluginJar = new JarFile(pluginFile.getAbsolutePath())) {
            ZipEntry configEntry = pluginJar.getEntry(Constants.PLUGIN_CONFIG);
            try (InputStream configStream = pluginJar.getInputStream(configEntry)) {
                info = new Gson().fromJson(new String(configStream.readAllBytes()), PluginInfo.class);
            }
        }

        PluginClassLoader loader = new PluginClassLoader(pluginFile.toURI().toURL(), serverClassLoader, pluginClassLoaderProvider);

        if (hasPluginId(info.getId())) {
            throw new Exception("Plugin id " + info.getId() + " conflict!!");
        }

        LOGGER.info("Constructing " + info.getName() + " (id: " + info.getId() + " )");
        IPancakePlugin plugin = (IPancakePlugin) loader.loadClass(info.getPluginClassName()).getConstructor()
                .newInstance();

        PluginData data = new PluginData(server, plugin, info, new PluginDataStorage(new File(pluginStorage.getDirectory(), info.getId())), loader);

        pluginMap.put(info.getId(), data);
        plugin.onLoad();

        LOGGER.info(info.getName() + " has been loaded");

        return data;
    }

    public void unloadPlugin(PluginData data) {
        PluginInfo info = data.getInfo();

        if (info == null) {
            LOGGER.warn("invalid PluginData?! " + data);
            return;
        }

        if (!hasPluginId(info.getId())) {
            LOGGER.warn("Can't find plugin " + info.getName() + " (id: " + info.getId() + " )");
            return;
        }

        IPancakePlugin plugin = data.getPlugin();

        LOGGER.info("Unloading " + info.getName() + " (id: " + info.getId() + " )");

        plugin.onUnload();

        pluginMap.remove(data.getInfo().getId());

        LOGGER.info("Unloaded " + info.getName() + " (id: " + info.getId() + " )");
    }

}
