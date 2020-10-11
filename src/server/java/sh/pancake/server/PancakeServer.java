/*
 * Created on Wed Sep 30 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixins;

import net.minecraft.server.dedicated.DedicatedServer;
import sh.pancake.launcher.IPancakeServer;
import sh.pancake.launcher.classloader.ClassLoaderProvider;
import sh.pancake.launcher.classloader.ServerClassLoader;
import sh.pancake.server.command.CommandManager;
import sh.pancake.server.event.EventManager;
import sh.pancake.server.event.IEvent;
import sh.pancake.server.event.IEventListener;
import sh.pancake.server.mod.ModManager;
import sh.pancake.server.network.NetworkManager;
import sh.pancake.server.plugin.PluginManager;

public class PancakeServer implements IPancakeServer {

    private static final String VERSION;

    static {
        try {
            VERSION = new String(PancakeServer.class.getClassLoader().getResourceAsStream("target_version").readAllBytes());
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private static final Logger LOGGER = LogManager.getLogger("PancakeServer");

    private long startTime;

    private ServerClassLoader serverClassLoader;

    private NetworkManager networkManager;

    private ModManager modManager;
    private PluginManager pluginManager;

    private CommandManager<IPancakeExtra> commandManager;

    private EventManager<IEvent, IEventListener> eventManager;

    private DedicatedServer minecraftServer;
    private ServerStartStatus startStatus;

    public PancakeServer() {
        this.startTime = -1;

        this.networkManager = null;

        this.modManager = null;
        this.pluginManager = null;

        this.eventManager = null;
        this.commandManager = null;

        this.minecraftServer = null;
        this.startStatus = ServerStartStatus.NOT_STARTED;
    }

    public String getVersion() {
        return PancakeServer.VERSION;
    }

    public long getStartTime() {
        return startTime;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public EventManager<IEvent, IEventListener> getEventManager() {
        return eventManager;
    }

    public ModManager getModManager() {
        return modManager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public CommandManager<IPancakeExtra> getCommandManager() {
        return commandManager;
    }

    // You can safely call getMinecraftServer() when it does not return NOT_STARTED
    public ServerStartStatus getStartStatus() {
        return startStatus;
    }

    public DedicatedServer getMinecraftServer() {
        return minecraftServer;
    }

    // Don't call it manually unless you know what you are doing!!
    public void addURLToClassPath(URL url) {
        serverClassLoader.addURL(url);
    }

    public void start(String[] args, ServerClassLoader classLoader, Runnable finishMixinInit) {
        this.serverClassLoader = classLoader;

        // Skip all transform of server classes
        serverClassLoader.addIgnoreTransform(PancakeServer.class.getPackageName());

        this.startTime = System.currentTimeMillis();

        ClassLoaderProvider extraClassLoaderProvider = new ClassLoaderProvider();
        extraClassLoaderProvider.addSubLoader(PancakeServer.class.getClassLoader());

        LOGGER.info("Running on " + Runtime.version().toString());
        LOGGER.info("Total Memory: " + (Math.floor(Runtime.getRuntime().maxMemory() / 1024 / 1024) / 1024d) + " GB");

        LOGGER.info("Server version: " + getVersion());
        LOGGER.info("Applying server arguments [ " + String.join(", ", args) + " ]");

        this.startStatus = ServerStartStatus.NOT_STARTED;

        this.networkManager = new NetworkManager(this);

        this.commandManager = new CommandManager<>();

        this.eventManager = new EventManager<>();

        this.modManager = new ModManager(this, Constants.MOD_DIRECTORY);
        this.pluginManager = new PluginManager(this, Constants.PLUGIN_DIRECTORY, extraClassLoaderProvider);

        finishMixinInit.run();

        // ah yes pancake
        Mixins.addConfiguration("pancake-config.json");

        loadAllMod();

        finishMixinInit.run();

        loadAllPlugin();

        List<String> argList = new ArrayList<>(Arrays.asList(args));

        // Kill ugly gui
        argList.add("nogui");

        try {
            net.minecraft.server.Main.main(argList.toArray(new String[0]));
        } catch (Throwable throwable) {
            LOGGER.fatal("Server start failed: " + throwable.getLocalizedMessage());
            throw new RuntimeException(throwable);
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
        pluginManager.forEach((pluginData) -> {
            try {
                pluginData.getPlugin().onServerPreInit();
            } catch (Throwable throwable) {
                LOGGER.error("Error while performing preinit to " + pluginData.getInfo().getId());
                throwable.printStackTrace();
            }
            
        });
    }

	public void onPostMCServerInit(DedicatedServer dedicatedServer) {
        if (startStatus != ServerStartStatus.PREINIT) throw new IllegalStateException("MinecraftServer is not Initialized");
        if (dedicatedServer != this.minecraftServer) throw new IllegalStateException("Different MinecraftServer instance??");
        this.startStatus = ServerStartStatus.POSTINIT;

        LOGGER.info("Performing post init...");

        modManager.forEach((modData) -> modData.getMod().onServerPostInit());
        pluginManager.forEach((pluginData) -> {
            try {
                pluginData.getPlugin().onServerPostInit();
            } catch (Throwable throwable) {
                LOGGER.error("Error while performing postinit to " + pluginData.getInfo().getId());
                throwable.printStackTrace();
            }
            
        });

        this.startStatus = ServerStartStatus.STARTED;
        LOGGER.info("MinecraftServer started!! Took " + ((System.currentTimeMillis() - startTime) / 1000f) + "s");
	}

}
