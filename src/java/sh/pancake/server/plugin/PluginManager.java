/*
 * Created on Thu Aug 19 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.plugin;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nullable;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.commands.SharedSuggestionProvider;
import sh.pancake.server.command.CommandAdvisor;
import sh.pancake.server.command.CommandExecutor;
import sh.pancake.server.command.CommandResult;
import sh.pancake.server.command.PancakeCommandStack;
import sh.pancake.server.event.EventDispatcher;
import sh.pancake.server.extension.Extension;
import sh.pancake.server.extension.ExtensionStore;
import sh.pancake.server.util.ExtensionUtil;

public class PluginManager implements EventDispatcher, CommandExecutor, CommandAdvisor {

    private final ExtensionStore<PluginInfo> store;

    private final ClassLoader rootClassLoader;

    public PluginManager(String namespace, ClassLoader rootClassLoader) {
        this.store = new ExtensionStore<>(namespace);
        this.rootClassLoader = rootClassLoader;
    }

    public String getNamespace() {
        return store.getNamespace();
    }

    public Collection<Extension<PluginInfo>> extensions() {
        return Collections.unmodifiableCollection(store);
    }

    @Nullable
    public Extension<PluginInfo> get(String id) {
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
    public void fillSuggestion(CommandNode<SharedSuggestionProvider> suggestion, PancakeCommandStack stack) {
        ExtensionUtil.fillSuggestion(store, suggestion, stack);
    }

    public boolean loadPlugin(Extension<PluginInfo> extension) {
        if (store.contains(extension)) return false;

        

        store.add(extension);
        return true;
    }

    public boolean unloadPlugin(String id) {
        return unloadPlugin(id, false);
    }

    public boolean unloadPlugin(String id, boolean recursive) {
        var ext = get(id);
        if (ext == null) return false;

        return unloadPlugin(ext, recursive);
    }

    public boolean unloadPlugin(Extension<PluginInfo> extension) {
        return unloadPlugin(extension, false);
    }

    public boolean unloadPlugin(Extension<PluginInfo> extension, boolean recursive) {
        if (!store.contains(extension)) return false;



        store.remove(extension);
        return true;
    }

    public boolean reloadPlugin(String id) {
        return reloadPlugin(id, false);
    }

    public boolean reloadPlugin(String id, boolean recursive) {
        var ext = get(id);
        if (ext == null) return false;

        return reloadPlugin(ext, recursive);
    }
    
    public boolean reloadPlugin(Extension<PluginInfo> extension) {
        return reloadPlugin(extension, false);
    }

    public boolean reloadPlugin(Extension<PluginInfo> extension, boolean recursive) {
        if (!store.contains(extension)) return false;



        return true;
    }

}
