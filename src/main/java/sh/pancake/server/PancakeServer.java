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

import net.minecraft.server.dedicated.DedicatedServer;
import sh.pancake.classloader.ClassLoaderProvider;
import sh.pancake.classloader.ModdedClassLoader;
import sh.pancake.common.storage.ObjectStorage;
import sh.pancake.server.command.CommandManager;
import sh.pancake.server.event.EventManager;
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

    private long startTime;

    private ModdedClassLoader serverLoader;

    private ModManager modManager;
    private PluginManager pluginManager;

    private CommandManager commandManager;

    private EventManager eventManager;

    private DedicatedServer minecraftServer;
    private ServerStartStatus startStatus;

    public PancakeServer() {
        this.startTime = -1;

        this.serverLoader = null;

        this.modManager = null;
        this.pluginManager = null;

        this.eventManager = null;
        this.commandManager = null;

        this.minecraftServer = null;
        this.startStatus = ServerStartStatus.NOT_STARTED;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getVersion() {
        return version;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public ModManager getModManager() {
        return modManager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    // You can safely call getMinecraftServer() when it does not return NOT_STARTED
    public ServerStartStatus getStartStatus() {
        return startStatus;
    }

    public DedicatedServer getMinecraftServer() {
        return minecraftServer;
    }
    
    public void start(String[] args, ModdedClassLoader loader, ObjectStorage serverDataStorage,
            Runnable finishMixinInit) {
        this.startTime = System.currentTimeMillis();

        this.serverLoader = loader;

        ClassLoaderProvider extraClassLoaderProvider = new ClassLoaderProvider();
        extraClassLoaderProvider.addSubLoader(this.serverLoader);

        LOGGER.info("Running on " + Runtime.version().toString());
        LOGGER.info("Total Memory: " + (Math.floor(Runtime.getRuntime().maxMemory() / 1024 / 1024) / 1024d) + " GB");

        LOGGER.info("Server version: " + getVersion());
        LOGGER.info("Applying server arguments [ " + String.join(", ", args) + " ]");

        // Force to use our configuration
        serverLoader.addIgnoreRes("log4j2.xml");

        // We have our own brigadier embed!
        serverLoader.addIgnoreRes("com/mojang/brigadier");

        Class<?> serverClass = null;
        try {
            serverClass = serverLoader.loadClass("net.minecraft.server.Main");
        } catch (Exception e) {
            LOGGER.fatal("Cannot load server entrypoint: " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

        this.startStatus = ServerStartStatus.NOT_STARTED;

        this.commandManager = new CommandManager();

        this.eventManager = new EventManager();

        this.modManager = new ModManager(this, Constants.MOD_DIRECTORY, serverLoader);
        this.pluginManager = new PluginManager(this, Constants.PLUGIN_DIRECTORY, extraClassLoaderProvider);

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

        Lists.newArrayList(modDir.listFiles((File file) -> file.getName().endsWith(".jar"))).parallelStream()
            .forEach((File file) -> {
                try {
                    modManager.loadMod(file);
                } catch (Throwable t) {
                    LOGGER.fatal("Cannot load mod from " + file.getName());
                    LOGGER.fatal("Server cannot start with mod error!!");
                    t.printStackTrace();

                    throw new RuntimeException(t);
                }
            }
        );
    }

    protected void loadAllPlugin() {
        File pluginDir = pluginManager.getPluginStorage().getDirectory();

        LOGGER.info("Loading plugins from " + pluginDir.getAbsolutePath());

        pluginDir.mkdirs();

        Lists.newArrayList(pluginDir.listFiles((File file) -> file.getName().endsWith(".jar"))).parallelStream()
            .forEach((File file) -> {
                try {
                    pluginManager.loadPlugin(file);
                } catch (Throwable t) {
                    LOGGER.error("Cannot load plugin from " + file.getName());
                    t.printStackTrace();
                }
            }
        );
    }

    public void onPreMCServerInit(DedicatedServer server) {
        if (startStatus != ServerStartStatus.NOT_STARTED) throw new IllegalStateException("Server is already started status?!");
        this.startStatus = ServerStartStatus.PREINIT;

        this.minecraftServer = server;

        LOGGER.info("MinecraftServer initialized. Performing pre init...");

        modManager.forEach((modData) -> modData.getMod().onServerPreInit());
        pluginManager.forEach((pluginData) -> pluginData.getPlugin().onServerPreInit());
    }

	public void onPostMCServerInit(DedicatedServer dedicatedServer) {
        if (startStatus != ServerStartStatus.PREINIT) throw new IllegalStateException("MinecraftServer is not Initialized");
        if (dedicatedServer != this.minecraftServer) throw new IllegalStateException("Different MinecraftServer instance??");
        this.startStatus = ServerStartStatus.POSTINIT;

        LOGGER.info("Performing post init...");

        modManager.forEach((modData) -> modData.getMod().onServerPostInit());
        pluginManager.forEach((pluginData) -> pluginData.getPlugin().onServerPostInit());

        this.startStatus = ServerStartStatus.STARTED;
        LOGGER.info("MinecraftServer started!! Took " + ((System.currentTimeMillis() - startTime) / 1000f) + "s");
	}

}
