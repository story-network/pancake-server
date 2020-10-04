/*
 * Created on Sun Oct 04 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

@Mixin(Commands.class)
public abstract class CommandsMixin {

    @Shadow
    private CommandDispatcher<CommandSourceStack> dispatcher;

    @Shadow
    abstract void fillUsableCommands(CommandNode<CommandSourceStack> root, CommandNode<SharedSuggestionProvider> suggestion, CommandSourceStack stack, Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> redirectMap);

    @Inject(method = "<init>*", at = @At("RETURN"))
    void onConstructor(CallbackInfo info) {
        PancakeServer server = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        server.getCommandManager().onMCCommandInit(dispatcher);
    }

    @Inject(method = "sendCommands", at = @At("RETURN"))
    public void onSendCommands(ServerPlayer player, CallbackInfo info) {
        PancakeServer server = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        List<CommandDispatcher<CommandSourceStack>> list = server.getCommandManager().getDispatcherList();
        Iterator<CommandDispatcher<CommandSourceStack>> iter = list.iterator();

        Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> redirectMap = new HashMap<>();

        RootCommandNode<SharedSuggestionProvider> rootSuggestion = new RootCommandNode<>();

        while (iter.hasNext()) {
            fillUsableCommands(iter.next().getRoot(), rootSuggestion, player.createCommandSourceStack(), redirectMap);
        }
        
        player.connection.send(new ClientboundCommandsPacket(rootSuggestion));
    }

}
