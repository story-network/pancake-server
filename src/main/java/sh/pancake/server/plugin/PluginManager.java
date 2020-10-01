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

import sh.pancake.classloader.CompositeClassLoader;
import sh.pancake.common.storage.DiskIOStorage;
import sh.pancake.server.Constants;
import sh.pancake.server.plugin.loader.PluginClassLoader;

/*
 *
 * Manage plugins. Can be used for interacting between plugins
 * 
 */
public class PluginManager {

    private static final Logger LOGGER = LogManager.getLogger("PluginManager");

    private DiskIOStorage pluginStorage;

    private CompositeClassLoader<PluginClassLoader> pluginRootClassLoader;

    private Map<String, PluginData> pluginMap;

    public PluginManager(String pluginFolderName, ClassLoader serverClassLoader) {
        this.pluginStorage = new DiskIOStorage(pluginFolderName);
        this.pluginRootClassLoader = new CompositeClassLoader<>(serverClassLoader);
        this.pluginMap = new ConcurrentHashMap<>();
    }

    public DiskIOStorage getPluginStorage() {
        return pluginStorage;
    }

    public CompositeClassLoader<PluginClassLoader> getPluginRootClassLoader() {
        return pluginRootClassLoader;
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

        PluginClassLoader loader = new PluginClassLoader(pluginFile.toURI().toURL(), pluginRootClassLoader);

        if (hasPluginId(info.getId())) {
            loader.close();
            throw new Exception("Plugin id " + info.getId() + " conflict!!");
        }

        LOGGER.info("Constructing " + info.getName() + " (id: " + info.getId() + " )");
        IPancakePlugin plugin = (IPancakePlugin) loader.loadClass(info.getPluginClassName()).getConstructor()
                .newInstance();

        PluginData data = new PluginData(plugin, info, loader);

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

        try {
            data.getPluginClassLoader().close();
        } catch (Exception e) {
            LOGGER.warn("Closing ClassLoader failed. Already closed?");
        } finally {
            LOGGER.info("Unloaded " + info.getName() + " (id: " + info.getId() + " )");
        }
    }

}
