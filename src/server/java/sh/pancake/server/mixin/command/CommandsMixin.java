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
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.commands.CommandRuntimeException;
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
    abstract void fillUsableCommands(CommandNode<CommandSourceStack> root,
            CommandNode<SharedSuggestionProvider> suggestion, CommandSourceStack stack,
            Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> redirectMap);

    @Inject(method = "<init>*", at = @At("RETURN"))
    void onConstructor(CallbackInfo info) {
        PancakeServer server = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        server.getCommandManager().onMCCommandInit(dispatcher);
    }

    @Inject(method = "sendCommands", at = @At("HEAD"), cancellable = true)
    public void onSendCommands(ServerPlayer player, CallbackInfo info) {
        // No we will merge every commands
        info.cancel();

        PancakeServer server = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        CommandSourceStack source = player.createCommandSourceStack();

        List<CommandDispatcher<CommandSourceStack>> list = server.getCommandManager().getDispatcherList();
        Iterator<CommandDispatcher<CommandSourceStack>> iter = list.iterator();

        Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> redirectMap = new HashMap<>();

        RootCommandNode<SharedSuggestionProvider> rootSuggestion = new RootCommandNode<>();

        redirectMap.put(dispatcher.getRoot(), new RootCommandNode<>());
        fillUsableCommands(dispatcher.getRoot(), rootSuggestion, source, redirectMap);

        while (iter.hasNext()) {
            fillUsableCommands(iter.next().getRoot(), rootSuggestion, source, redirectMap);
        }

        player.connection.send(new ClientboundCommandsPacket(rootSuggestion));
    }

    @Redirect(method = "performCommand", at = @At(value = "INVOKE", target = "com/mojang/brigadier/CommandDispatcher.execute(Lcom/mojang/brigadier/StringReader;Ljava/lang/Object;)I"))
    public int commandExecuteProxy(CommandDispatcher<CommandSourceStack> dispatcher, StringReader reader, Object obj) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack) obj;
        PancakeServer server = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        int executionCount = 0;

        boolean hasCommand = false;

        try {
            executionCount += server.getCommandManager().performCommand(reader, source);
            hasCommand = true;
        } catch (CommandRuntimeException runtimeEx) {
            
        } catch (CommandSyntaxException syntaxEx) {
            
        }

        try {
            executionCount += dispatcher.execute(reader, source);
            hasCommand = true;
        } catch (CommandRuntimeException runtimeEx) {
            if (!hasCommand) throw runtimeEx;
        } catch (CommandSyntaxException syntaxEx) {
            if (!hasCommand) throw syntaxEx;
        }

        return executionCount;
    }

}
