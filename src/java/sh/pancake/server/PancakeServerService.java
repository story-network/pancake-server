/*
 * Created on Sun Aug 08 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.spongepowered.asm.mixin.Mixins;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
import sh.pancake.launcher.IPancakeServer;
import sh.pancake.launcher.classloader.DynamicURLClassLoader;
import sh.pancake.server.console.ServerConsole;
import sh.pancake.server.extension.Extension;
import sh.pancake.server.extension.ExtensionStore;
import sh.pancake.server.impl.event.concurrent.server.PhaseChangedEvent;
import sh.pancake.server.mod.FileModLoader;
import sh.pancake.server.mod.ModInfo;
import sh.pancake.server.mod.ModInitializer;
import sh.pancake.server.mod.ModManager;
import sh.pancake.server.network.ServerNetworkManager;
import sh.pancake.server.plugin.FilePluginLoader;
import sh.pancake.server.plugin.PluginInfo;
import sh.pancake.server.plugin.PluginManager;
import sh.pancake.server.util.ExecutorServiceUtil;

/**
 * Singleton object managing PancakeServer.
 *
 * Initialize mods and plugins and supply to PancakeServer.
 * Bridge MC server and PancakeServer.
 */
public class PancakeServerService implements IPancakeServer {

    private static PancakeServerService service;

    private static final Logger LOGGER = LogManager.getLogger();

    @Nullable
    public static PancakeServerService getService() {
        return service;
    }

    private PancakeServer server;

    private final ServerConsole console;

    private DedicatedServer mcServer;

    private ServerPhase phase;

    private long startTime;

    public PancakeServerService() {
        if (service != null) throw new RuntimeException("Cannot initialize more than one service");
        service = this;

        this.server = null;
        this.console = new ServerConsole(this);

        this.phase = ServerPhase.NOT_STARTED;
        this.startTime = -1;

        this.mcServer = null;
    }

    @Nullable
    public PancakeServer getServer() {
        return server;
    }

    @Nullable
    public DedicatedServer getDedicatedServer() {
        return mcServer;
    }

    public ServerPhase getPhase() {
        return phase;
    }

    public long getStartTime() {
        return startTime;
    }

    private void ensurePhase(ServerPhase current, ServerPhase next) {
        if (phase.getIndex() > current.getIndex()) throw new RuntimeException("Server already passed phase: " + current);
        ServerPhase last = phase;

        phase = next;

        LOGGER.debug("Phase " + current + " -> " + next);
        if (server != null) {
            server.dispatchEvent(new PhaseChangedEvent(last, next));
        }
    }

    @Override
    public void start(String[] args, DynamicURLClassLoader loader, Runnable finishMixin) {
        ensurePhase(ServerPhase.NOT_STARTED, ServerPhase.PREPARING);

        this.startTime = System.currentTimeMillis();

        LOGGER.info("Running on Java " + Runtime.version().toString());
        LOGGER.info("Total Memory: " + (Math.floor(Runtime.getRuntime().maxMemory() / 1024 / 1024) / 1024d) + " GB");

        LOGGER.info("Applying server arguments [ " + String.join(", ", args) + " ]");

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (getPhase().getIndex() <= ServerPhase.STARTED.getIndex()) stopServer();
        }));

        LOGGER.info("Loading external libraries...");
        loadLibraries(loader);

        ExtensionStore<ModInfo> modStore = new ExtensionStore<>("pancake_mod");
        ModManager modManager = new ModManager(modStore);
        
        PluginManager pluginManager = new PluginManager("pancake_plugin", PancakeServerService.class.getClassLoader());

        LOGGER.info("Starting PancakeServer...");
        this.server = new PancakeServer(this, modManager, pluginManager);

        LOGGER.info("Constructing mods...");
        constructMods(modStore, loader);

        // Add server Mixin
        Mixins.addConfiguration("pancake-config.json");

        finishMixin.run();

        doPreInit(loader);

        net.minecraft.server.Main.main(args);
    }

    /**
     * Load libraries from libraries directory
     * @param loader
     */
    private void loadLibraries(DynamicURLClassLoader loader) {
        File librariesDir = new File("libraries");

        librariesDir.mkdirs();

        for (var file : librariesDir.listFiles((dir, name) -> name.endsWith(".jar"))) {
            try {
                loader.addURL(file.toURI().toURL());
                LOGGER.info("Loaded external library " + file.getName() + " to classpath");
            } catch (Exception e) {
                LOGGER.fatal("Cannot not load external library file" + file.getName() + ".");
                throw new RuntimeException(e);
            }
        }
    }

    public void startConsole() {
        try {
            LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    
            ClassLoader loader = PancakeServerService.class.getClassLoader();

            ctx.setConfigLocation(
                loader.getResource("log4j2-server.xml").toURI()
            );

            console.start();
        } catch (URISyntaxException e) {
            LOGGER.error("Cannot switch to ServerConsole. Default console will be used.");
        }
    }

    /**
     * PreInitialization stage.
     * Plugins are constructed.
     *
     * @param loader
     */
    private void doPreInit(DynamicURLClassLoader loader) {
        ensurePhase(ServerPhase.PREPARING, ServerPhase.PRE_INIT);

        LOGGER.info("Loading plugins...");
        PancakeServerService.class.getClassLoader();
        loadPlugins(server.getPluginManager());
    }

    /**
     * Construct mods from mods directory
     * @param loader
     */
    private void constructMods(ExtensionStore<ModInfo> modStore, DynamicURLClassLoader loader) {
        File modDirectory = new File(Constants.MOD_DIRECTORY);
        modDirectory.mkdirs();

        try {
            for (var file : modDirectory.listFiles()) {
                if (file.isDirectory()) {
                    continue;
                } else if (!file.getName().endsWith(".jar")) {
                    LOGGER.warn("File " + file.getName() + " is not jar file. Skipping...");
                    continue;
                }
    
                try {
                    FileModLoader modLoader = new FileModLoader(file);
                    Extension<ModInfo> ext = modLoader.load();
                    modStore.add(ext);
                } catch (Exception e) {
                    LOGGER.warn("File " + file.getName() + " is invalid mod jar file. Skipping...", e);
                }
            }

            ModInitializer initializer = new ModInitializer(server, loader);
            initializer.load(modStore);
        } catch (Exception e) {
            LOGGER.fatal("Error while loading mods. server cannot start.");
            throw new RuntimeException(e);
        }
    }

    /**
     * Load plugins from plugins directory
     * @param loader
     */
    private void loadPlugins(PluginManager manager) {
        File pluginDirectory = new File(Constants.PLUGIN_DIRECTORY);
        pluginDirectory.mkdirs();

        List<Callable<Void>> taskList = new ArrayList<>();

        for (var file : pluginDirectory.listFiles()) {
            if (file.isDirectory()) {
                continue;
            } else if (!file.getName().endsWith(".jar")) {
                LOGGER.warn("File " + file.getName() + " is not jar file. Skipping...");
                continue;
            }

            try {
                FilePluginLoader pluginLoader = new FilePluginLoader(file);
                Extension<PluginInfo> ext = pluginLoader.load();
                
                taskList.add(() -> {
                    manager.loadPlugin(ext);
                    return null;
                });
            } catch (Exception e) {
                LOGGER.warn("File " + file.getName() + " is invalid mod jar file. Skipping...", e);
            }
        }

        try {
            ExecutorServiceUtil.all(server.getExecutorService(), taskList);
        } catch (Exception e) {
            LOGGER.error("Error while loading some plugins", e);
        }
    }

    /**
     * Initialization stage.
     * Minecraft server instance created.
     *
     * @param loader
     */
    public void doInit(DedicatedServer mcServer) {
        ensurePhase(ServerPhase.PRE_INIT, ServerPhase.INIT);
        this.mcServer = mcServer;

        ServerNetworkManager network = new ServerNetworkManager(server, mcServer.getConnection());
        server.setNetworkManager(network);
    }

    public void doPostInit() {
        ensurePhase(ServerPhase.INIT, ServerPhase.POST_INIT);

        doStarted();
    }

    private void doStarted() {
        ensurePhase(ServerPhase.POST_INIT, ServerPhase.STARTED);

    }

    /**
     * Stop PancakeServer and Minecraft server.
     * Server will wait to start first if not started yet.
     */
    public void stopServer() {
        try {
            ensurePhase(ServerPhase.STARTED, ServerPhase.STOPPING);
        } catch (Exception e) {
            return;
        }

        DedicatedServer mcServer = getDedicatedServer();
        if (mcServer != null) {
            // Finish mod, plugins
            mcServer.submit(() -> {}).join();
            
            // Wait MC server to finish
            mcServer.halt(true);
            mcServer = null;
        }

        server.close();

        try {
            TerminalConsoleAppender.close();
        } catch (IOException e) {
            LOGGER.error("Error while closing console. Assuming console already closed.", e);
        }

        ensurePhase(ServerPhase.STOPPING, ServerPhase.FINISHED);
    }

}
