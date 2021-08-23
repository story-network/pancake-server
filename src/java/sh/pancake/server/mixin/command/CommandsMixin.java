/*
 * Created on Sun Oct 04 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.RootCommandNode;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.server.level.ServerPlayer;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.command.BrigadierUtil;
import sh.pancake.server.command.CommandResult;

@Mixin(Commands.class)
public abstract class CommandsMixin {

    @Shadow
    @Final
    private CommandDispatcher<CommandSourceStack> dispatcher;

    @Inject(method = "sendCommands", at = @At("HEAD"), cancellable = true)
    public void sendCommandsPre(ServerPlayer player, CallbackInfo info) {
        if (info.isCancelled()) return;

        // No we will merge every commands
        info.cancel();

        RootCommandNode<SharedSuggestionProvider> suggestion = new RootCommandNode<>();

        CommandSourceStack source = player.createCommandSourceStack();

        BrigadierUtil.addSuggestion(suggestion, dispatcher.getRoot(), source);

        PancakeServer server = PancakeServerService.getService().getServer();
        if (server != null) {
            server.fillSuggestion(suggestion, server.createCommandStack(source));
        }

        player.connection.send(new ClientboundCommandsPacket(suggestion));
    }

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
