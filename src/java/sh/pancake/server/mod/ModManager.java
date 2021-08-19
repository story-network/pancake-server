/*
 * Created on Wed Aug 18 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mod;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nullable;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import sh.pancake.server.command.CommandExecutor;
import sh.pancake.server.command.CommandResult;
import sh.pancake.server.command.PancakeCommandStack;
import sh.pancake.server.event.EventDispatcher;
import sh.pancake.server.extension.Extension;
import sh.pancake.server.extension.ExtensionStore;
import sh.pancake.server.util.ExtensionUtil;

public class ModManager implements EventDispatcher, CommandExecutor {
    
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

}
