/*
 * Created on Tue Aug 10 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import sh.pancake.server.command.BrigadierUtil;
import sh.pancake.server.command.CommandAdvisor;
import sh.pancake.server.command.CommandExecutor;
import sh.pancake.server.command.CommandResult;
import sh.pancake.server.command.PancakeCommandStack;
import sh.pancake.server.event.EventDispatcher;
import sh.pancake.server.impl.command.ServerCommands;
import sh.pancake.server.impl.event.server.ServerReloadEvent;
import sh.pancake.server.mod.ModManager;
import sh.pancake.server.network.ServerNetworkManager;
import sh.pancake.server.network.payload.PayloadCollector;
import sh.pancake.server.network.payload.GlobalPayloadListener;
import sh.pancake.server.plugin.PluginManager;

public class PancakeServer
        implements EventDispatcher, CommandAdvisor, CommandExecutor, PayloadCollector, GlobalPayloadListener {

    private final PancakeServerService service;

    private final ServerCommands serverCommands;

    private final ExecutorService executorService;

    private final ModManager modManager;
    private final PluginManager pluginManager;

    private ServerNetworkManager network;

    public PancakeServer(PancakeServerService service, ModManager modManager, PluginManager pluginManager) {
        this.service = service;

        this.serverCommands = new ServerCommands("pancake", this);

        this.executorService = Executors.newCachedThreadPool();

        this.modManager = modManager;
        this.pluginManager = pluginManager;

        this.network = null;
    }

    public PancakeServerService getService() {
        return service;
    }

    public ServerCommands getServerCommands() {
        return serverCommands;
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
        CommandResult modResult = modManager.executeCommand(reader, stack);
        if (modResult.isExecuted()) return modResult;

        CommandResult pluginResult = pluginManager.executeCommand(reader, stack);
        if (pluginResult.isExecuted()) return pluginResult;

        return serverCommands.executeCommand(reader, stack);
    }

    @Override
    public void fillSuggestion(
        CommandNode<SharedSuggestionProvider> suggestion,
        PancakeCommandStack stack,
        Map<CommandNode<PancakeCommandStack>, CommandNode<SharedSuggestionProvider>> redirectMap
    ) {
        modManager.fillSuggestion(suggestion, stack, redirectMap);
        pluginManager.fillSuggestion(suggestion, stack, redirectMap);
        serverCommands.fillSuggestion(suggestion, stack, redirectMap);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CompletableFuture<Suggestions> getCompletionSuggestions(StringReader reader, PancakeCommandStack stack) {
        CompletableFuture<Suggestions>[] futures = new CompletableFuture[] {
            modManager.getCompletionSuggestions(new StringReader(reader), stack),
            pluginManager.getCompletionSuggestions(new StringReader(reader), stack),
            serverCommands.getCompletionSuggestions(new StringReader(reader), stack)
        };

        return BrigadierUtil.mergeSuggestionTasks(reader.getString(), Arrays.asList(futures));
    }

    /**
     * Wrap CommandSourceStack and create to PancakeCommandStack
     * 
     * @param inner
     * @return
     */
    public PancakeCommandStack createCommandStack(CommandSourceStack inner) {
        return new PancakeCommandStack(this, inner);
    }

    @Override
    public void fillPayloadChannels(Set<ResourceLocation> set) {
        modManager.fillPayloadChannels(set);
        pluginManager.fillPayloadChannels(set);
    }

    @Override
    public void processPayload(ResourceLocation identifier, Channel channel, ByteBuf buf) {
        modManager.processPayload(identifier, channel, buf);
        pluginManager.processPayload(identifier, channel, buf);
    }

    public CompletableFuture<Void> reload() {
        return reload(false);
    }

    /**
     * Reload reloadable resources (plugins)
     * 
     * @param fullReload true if minecraft server reloaded. Indicating mod, plugin need to refresh hooked resources.
     */
    public CompletableFuture<Void> reload(boolean fullReload) {
        dispatchEvent(new ServerReloadEvent(fullReload));

        return CompletableFuture.completedFuture(null);
    }

    protected void close() {
        if (network != null)
            network.close();

        executorService.shutdownNow();
    }

}
