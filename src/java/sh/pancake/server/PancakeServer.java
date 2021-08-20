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
import com.mojang.brigadier.tree.CommandNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import sh.pancake.server.command.CommandAdvisor;
import sh.pancake.server.command.CommandExecutor;
import sh.pancake.server.command.CommandResult;
import sh.pancake.server.command.PancakeCommandStack;
import sh.pancake.server.event.EventDispatcher;
import sh.pancake.server.mod.ModManager;
import sh.pancake.server.network.ServerNetworkManager;
import sh.pancake.server.plugin.PluginManager;

public class PancakeServer implements EventDispatcher, CommandAdvisor, CommandExecutor {

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
    public CommandResult executeCommand(StringReader reader, PancakeCommandStack stack) throws CommandSyntaxException {
        int lastCursor = reader.getCursor();

        CommandResult result = modManager.executeCommand(reader, stack);

        if (result.isExecuted()) return result;

        reader.setCursor(lastCursor);

        return pluginManager.executeCommand(reader, stack);
    }

    @Override
    public void fillSuggestion(CommandNode<SharedSuggestionProvider> suggestion, PancakeCommandStack stack) {
        modManager.fillSuggestion(suggestion, stack);
        pluginManager.fillSuggestion(suggestion, stack);
    }

    /**
     * Wrap CommandSourceStack and create to PancakeCommandStack
     * @param inner
     * @return
     */
    public PancakeCommandStack createCommandStack(CommandSourceStack inner) {
        return new PancakeCommandStack(this, inner);
    }

    protected void close() {
        if (network != null) network.close();
    }

}
