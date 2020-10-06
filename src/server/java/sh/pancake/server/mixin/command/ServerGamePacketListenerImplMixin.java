/*
 * Created on Sun Oct 04 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.command;

import java.util.ArrayList;
import java.util.List;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import sh.pancake.common.util.AsyncTask;
import sh.pancake.launcher.PancakeLauncher;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.command.CommandStack;
import sh.pancake.server.command.ICommandStack;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @Shadow()
    private MinecraftServer server;

    @Shadow
    public abstract void send(Packet<?> var1);

    @Inject(method = "handleCustomCommandSuggestions", at = @At("HEAD"), cancellable = true)
    public void onHandleCustomCommandSuggestions(ServerboundCommandSuggestionPacket packet, CallbackInfo info) {
        info.cancel();

        PacketUtils.ensureRunningOnSameThread(packet, (ServerGamePacketListenerImpl) (Object) this, player.getLevel());

        PancakeServer pancakeServer = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        int resId = packet.getId();

        StringReader reader = new StringReader(packet.getCommand());
        if (reader.canRead() && reader.peek() == '/') reader.skip();

        CommandSourceStack source = player.createCommandSourceStack();
        ICommandStack stack = new CommandStack(pancakeServer, source);

        AsyncTask<Suggestions[]> suggestionsTask = pancakeServer.getCommandManager().createSuggestionsListAsync(stack, reader);

        suggestionsTask.then((suggestionsList) -> {
            if (suggestionsList.length < 1) return;

            List<Suggestion> suggestionList = new ArrayList<>();

            CommandDispatcher<CommandSourceStack> mcDispatcher = server.getCommands().getDispatcher();
            Suggestions mcSuggestions = mcDispatcher.getCompletionSuggestions(mcDispatcher.parse(reader, source)).join();
            
            for (Suggestions suggestions : suggestionsList) suggestionList.addAll(suggestions.getList());

            // Fill mc suggestions
            suggestionList.addAll(mcSuggestions.getList());

            Suggestions rootSuggestions = new Suggestions(suggestionsList[0].getRange(), suggestionList);

            send(new ClientboundCommandSuggestionsPacket(resId, rootSuggestions));
        });
    }
    
}
