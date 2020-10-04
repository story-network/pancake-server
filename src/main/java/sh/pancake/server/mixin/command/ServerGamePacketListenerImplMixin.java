/*
 * Created on Sun Oct 04 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
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
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import sh.pancake.common.util.AsyncTask;
import sh.pancake.launcher.PancakeLauncher;
import sh.pancake.server.PancakeServer;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @Shadow
    public abstract void send(Packet<?> var1);

    @Inject(method = "handleCustomCommandSuggestions", at = @At("RETURN"))
    public void onHandleCustomCommandSuggestions(ServerboundCommandSuggestionPacket packet, CallbackInfo info) {
        PancakeServer server = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        List<CommandDispatcher<CommandSourceStack>> dispatcherList = server.getCommandManager().getDispatcherList();
        Iterator<CommandDispatcher<CommandSourceStack>> iter = dispatcherList.iterator();

        int resId = packet.getId();

        StringReader reader = new StringReader(packet.getCommand());
        if (reader.canRead() && reader.peek() == '/') reader.skip();

        CommandSourceStack sourceStack = player.createCommandSourceStack();

        List<AsyncTask<Suggestions>> taskList = new ArrayList<>(dispatcherList.size());
        while (iter.hasNext()) {
            CommandDispatcher<CommandSourceStack> dispatcher = iter.next();

            ParseResults<CommandSourceStack> res = dispatcher.parse(reader, sourceStack);

            taskList.add(new AsyncTask<Suggestions>(dispatcher.getCompletionSuggestions(res)::join));
        }

        AsyncTask<Suggestions[]> suggestionsTask = AsyncTask.all(taskList.toArray(new AsyncTask[0]));

        suggestionsTask.then((suggestionsList) -> {
            if (suggestionsList.length < 1) return;

            List<Suggestion> suggestionList = new ArrayList<>();
            
            for (Suggestions suggestions : suggestionsList) suggestionList.addAll(suggestions.getList());

            Suggestions rootSuggestions = new Suggestions(suggestionsList[0].getRange(), suggestionList);

            send(new ClientboundCommandSuggestionsPacket(resId, rootSuggestions));
        });
    }

    @Inject(method = "handleCommand", at = @At("RETURN"))
    private void onHandleCommand(String rawCommand, CallbackInfo info) {
        PancakeServer server = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        server.getCommandManager().performCommand(player.createCommandSourceStack(), rawCommand);
    }
    
}
