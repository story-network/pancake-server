/*
 * Created on Wed Aug 18 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mod;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import sh.pancake.server.command.CommandAdvisor;
import sh.pancake.server.command.CommandExecutor;
import sh.pancake.server.command.CommandResult;
import sh.pancake.server.command.PancakeCommandStack;
import sh.pancake.server.event.EventDispatcher;
import sh.pancake.server.extension.Extension;
import sh.pancake.server.extension.ExtensionStore;
import sh.pancake.server.network.payload.PayloadCollector;
import sh.pancake.server.network.payload.GlobalPayloadListener;
import sh.pancake.server.util.ExtensionUtil;

public class ModManager implements EventDispatcher, CommandExecutor, CommandAdvisor, PayloadCollector, GlobalPayloadListener {
    
    private final ExtensionStore<ModInfo> store;

    public ModManager(ExtensionStore<ModInfo> store) {
        this.store = store;
    }

    public String getNamespace() {
        return store.getNamespace();
    }

    public Collection<Extension<ModInfo>> extensions() {
        return Collections.unmodifiableCollection(store);
    }

    @Nullable
    public Extension<ModInfo> get(String id) {
        return store.get(id);
    }

    @Override
    public void dispatchEvent(Object event) {
        ExtensionUtil.dispatchEvent(store, event);
    }

    @Override
    public CommandResult executeCommand(StringReader reader, PancakeCommandStack stack) throws CommandSyntaxException {
        return ExtensionUtil.dispatchCommand(store, reader, stack);
    }

    @Override
    public void fillSuggestion(
        CommandNode<SharedSuggestionProvider> suggestion,
        PancakeCommandStack stack,
        Map<CommandNode<PancakeCommandStack>, CommandNode<SharedSuggestionProvider>> redirectMap
    ) {
        ExtensionUtil.fillSuggestion(store, suggestion, stack, redirectMap);
    }

    @Override
    public void fillPayloadChannels(Set<ResourceLocation> set) {
        ExtensionUtil.fillPayloadChannels(store, set);
    }

    @Override
    public void processPayload(ResourceLocation identifier, Channel channel, ByteBuf buf) {
        ExtensionUtil.processPayload(store, identifier, channel, buf);
    }

}
