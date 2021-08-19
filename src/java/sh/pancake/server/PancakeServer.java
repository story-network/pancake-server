/*
 * Created on Tue Aug 10 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.dedicated.DedicatedServer;
import sh.pancake.server.command.CommandExecutor;
import sh.pancake.server.command.PancakeCommandStack;
import sh.pancake.server.event.EventDispatcher;
import sh.pancake.server.mod.ModManager;
import sh.pancake.server.network.ServerNetworkManager;
import sh.pancake.server.plugin.PluginManager;

public class PancakeServer implements EventDispatcher, CommandExecutor {

    private static final Logger LOGGER = LogManager.getLogger();

    private final PancakeServerService service;

    private final ExecutorService executorService;

    private final ModManager modManager;
    private final PluginManager pluginManager;

    private ServerNetworkManager network;

    protected PancakeServer(PancakeServerService service, ModManager modManager, PluginManager pluginManager) {
        this.service = service;

        this.executorService = Executors.newCachedThreadPool();

        this.modManager = modManager;
        this.pluginManager = pluginManager;

        this.network = null;
    }

    public PancakeServerService getService() {
        return service;
    }

    public ModManager getModManager() {
        return modManager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    @Nullable
    public ServerNetworkManager getNetworkManager() {
        return network;
    }

    protected void setNetworkManager(ServerNetworkManager network) {
        this.network = network;
    }

    @Override
    public void dispatchEvent(Object event) {
        // LOGGER.info("Dispatching event " + event);
        modManager.dispatchEvent(event);
    }

    @Override
    public int executeCommand(StringReader reader, PancakeCommandStack stack) throws CommandSyntaxException {
        try {
            return modManager.executeCommand(reader, stack);
        } catch (CommandSyntaxException e) {
            if (e.getType() != CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()) {
                throw e;
            }

            reader.setCursor(0);
        }

        return pluginManager.executeCommand(reader, stack);
    }

    /**
     * Create new PancakeCommandStack
     *
     * @return New PancakeCommandStack
     * @throws IllegalStateException Minecraft server instance is not created
     */
    public PancakeCommandStack createCommandStack() throws IllegalStateException {
        DedicatedServer server = service.getDedicatedServer();
        if (server == null) throw new IllegalStateException("Minecraft server instance is not created");

        return new PancakeCommandStack(this, server.createCommandSourceStack());
    }

    protected void close() {
        if (network != null) network.close();
    }

}
