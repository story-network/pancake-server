/*
 * Created on Sun Oct 04 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.command;

import java.util.HashMap;
import java.util.Map;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.command.CommandResult;

@Mixin(Commands.class)
public abstract class CommandsMixin {

    @Shadow
    abstract void fillUsableCommands(CommandNode<CommandSourceStack> root, CommandNode<SharedSuggestionProvider> suggestion, CommandSourceStack stack, Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> redirectMap);

    /*
    @Inject(method = "sendCommands", at = @At("HEAD"), cancellable = true)
    public void sendCommandsPre(ServerPlayer player, CallbackInfo info) {
        if (info.isCancelled()) return;

        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) return;

        // No we will merge every commands
        info.cancel();

        Map<CommandNode<?>, CommandNode<SharedSuggestionProvider>> redirectMap = new HashMap<>();
        RootCommandNode<SharedSuggestionProvider> rootSuggestion = new RootCommandNode<>();
        redirectMap.put(dispatcher.getRoot(), rootSuggestion);

        nestedDispatcher.fillUsableCommandList(rootSuggestion, new CommandStack(nestedDispatcher.getPancakeServer(), player.createCommandSourceStack()), redirectMap);

        player.connection.send(new ClientboundCommandsPacket(rootSuggestion));
    }
    */

    @Redirect(method = "performCommand", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;execute(Lcom/mojang/brigadier/StringReader;Ljava/lang/Object;)I"))
    public int executeDispatcher(CommandDispatcher<CommandSourceStack> dispatcher, StringReader reader, Object source) throws CommandSyntaxException {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server != null) {
            int lastCursor = reader.getCursor();

            CommandResult result = server.executeCommand(reader, server.createCommandStack((CommandSourceStack) source));
            if (result.isExecuted()) return result.getReturned();

            reader.setCursor(lastCursor);
        }

        return dispatcher.execute(reader, (CommandSourceStack) source);
    }

}
