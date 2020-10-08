/*
 * Created on Sun Oct 04 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.command;

import java.util.HashMap;
import java.util.Map;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.server.level.ServerPlayer;
import sh.pancake.launcher.PancakeLauncher;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.command.CommandStack;
import sh.pancake.server.command.NestedCommandDispatcher;

@Mixin(Commands.class)
public abstract class CommandsMixin {

    // Replace to NestedCommandDispatcher now we can manage multiple dispatchers efficiently
    @Shadow
    private CommandDispatcher<CommandSourceStack> dispatcher = new NestedCommandDispatcher(
        (PancakeServer) PancakeLauncher.getLauncher().getServer(),
        ((PancakeServer) PancakeLauncher.getLauncher().getServer()).getCommandManager());

    @Shadow
    abstract void fillUsableCommands(CommandNode<CommandSourceStack> root, CommandNode<SharedSuggestionProvider> suggestion, CommandSourceStack stack, Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> redirectMap);

    @Inject(method = "<init>*", at = @At("RETURN"))
    void onConstructor(CallbackInfo info) {
        if (info.isCancelled()) return;
        PancakeServer server = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        server.getCommandManager().onMCCommandInit(dispatcher);
    }

    @Inject(method = "sendCommands", at = @At("HEAD"), cancellable = true)
    public void onSendCommands(ServerPlayer player, CallbackInfo info) {
        if (info.isCancelled()) return;
        // No we will merge every commands
        info.cancel();
        
        NestedCommandDispatcher nestedDispatcher = (NestedCommandDispatcher) dispatcher;

        Map<CommandNode<?>, CommandNode<SharedSuggestionProvider>> redirectMap = new HashMap<>();
        RootCommandNode<SharedSuggestionProvider> rootSuggestion = new RootCommandNode<>();
        redirectMap.put(dispatcher.getRoot(), rootSuggestion);

        nestedDispatcher.fillUsableCommandList(rootSuggestion, new CommandStack(nestedDispatcher.getPancakeServer(), player.createCommandSourceStack()), redirectMap);

        player.connection.send(new ClientboundCommandsPacket(rootSuggestion));
    }

}
