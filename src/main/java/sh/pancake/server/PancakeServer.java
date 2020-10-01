/*
 * Created on Wed Sep 30 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixins;

import sh.pancake.classloader.ModdedClassLoader;
import sh.pancake.common.storage.ObjectStorage;
import sh.pancake.server.mod.ModManager;
import sh.pancake.server.plugin.PluginManager;

public class PancakeServer implements IPancakeServer {

    private static final Logger LOGGER = LogManager.getLogger("PancakeServer");

    private static final String version;

    static {
        try {
            version = new String(
                    PancakeServer.class.getClassLoader().getResourceAsStream("target_version").readAllBytes());
        } catch (IOException e) {
            LOGGER.fatal("Cannot load target version");

            throw new RuntimeException(e);
        }
    }

    private ModdedClassLoader serverLoader;

    private ModManager modManager;
    private PluginManager pluginManager;

    public PancakeServer() {
        this.serverLoader = null;
        this.modManager = null;
        this.pluginManager = null;
    }

    public String getVersion() {
        return version;
    }

    public ModManager getModManager() {
        return modManager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public void start(String[] args, ModdedClassLoader loader, ObjectStorage serverDataStorage,
            Runnable finishMixinInit) {
        this.serverLoader = loader;

        LOGGER.info("Running on " + Runtime.version().toString());
        LOGGER.info("Total Memory: " + (Math.floor(Runtime.getRuntime().maxMemory() / 1024 / 1024) / 1024d) + " GB");

        LOGGER.info("Server version: " + getVersion());
        LOGGER.info("Applying server arguments [ " + String.join(", ", args) + " ]");

        // Force to use our configuration
        serverLoader.addIgnoreRes("log4j2.xml");

        Class<?> serverClass = null;
        try {
            serverClass = serverLoader.loadClass("net.minecraft.server.Main");
        } catch (Exception e) {
            LOGGER.fatal("Cannot load server entrypoint: " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

        this.modManager = new ModManager(Constants.MOD_DIRECTORY, serverLoader);
        this.pluginManager = new PluginManager(Constants.PLUGIN_DIRECTORY, serverLoader);

        // ah yes pancake
        Mixins.addConfiguration("pancake-config.json");

        loadAllMod();

        finishMixinInit.run();

        loadAllPlugin();

        List<String> argList = new ArrayList<>(Arrays.asList(args));

        // Kill ugly gui
        argList.add("nogui");

        try {
            serverClass.getMethod("main", String[].class).invoke(null,
                    (Object) argList.toArray(new String[argList.size()]));
        } catch (Exception e) {
            LOGGER.fatal("Server start failed: " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    protected void loadAllMod() {
        File modDir = modManager.getModStorage().getDirectory();

        LOGGER.info("Loading mods from " + modDir.getAbsolutePath());

        modDir.mkdirs();
    }

    protected void loadAllPlugin() {
        File pluginDir = pluginManager.getPluginStorage().getDirectory();

        LOGGER.info("Loading plugins from " + pluginDir.getAbsolutePath());

        pluginDir.mkdirs();

        Lists.newArrayList(pluginDir.listFiles((File file) -> file.getName().endsWith(".jar"))).parallelStream()
                .forEach((File file) -> {
                    try {
                        pluginManager.loadPlugin(file);
                    } catch (Exception e) {
                        LOGGER.error("Cannot load plugin from " + file.getName());
                    }
            });
    }

}
