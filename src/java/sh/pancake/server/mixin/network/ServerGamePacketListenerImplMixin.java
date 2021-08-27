/*
 * Created on Sat Aug 21 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.network;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.command.BrigadierUtil;
import sh.pancake.server.command.PancakeCommandStack;
import sh.pancake.server.impl.event.player.PayloadMessageEvent;
import sh.pancake.server.impl.event.player.PlayerChatEvent;
import sh.pancake.server.impl.event.player.PlayerCloseMenuEvent;
import sh.pancake.server.impl.event.player.PlayerCommandEvent;
import sh.pancake.server.impl.event.player.PlayerLeaveChatEvent;
import sh.pancake.server.impl.event.player.PlayerDropItemEvent;
import sh.pancake.server.impl.event.player.PlayerHandAnimateEvent;
import sh.pancake.server.impl.event.player.PlayerJumpEvent;
import sh.pancake.server.impl.event.player.PlayerPaddleBoatEvent;
import sh.pancake.server.impl.event.player.PlayerResourcePackStatusEvent;
import sh.pancake.server.impl.event.player.PlayerToggleSneakEvent;
import sh.pancake.server.impl.event.player.PlayerToggleSprintEvent;
import sh.pancake.server.impl.event.player.PlayerVehicleInputEvent;
import sh.pancake.server.impl.network.Chat;
import sh.pancake.server.mixin.accessor.ConnectionAccessor;
import sh.pancake.server.mixin.accessor.ServerboundCustomPayloadPacketAccessor;
import sh.pancake.server.mixin.accessor.ServerboundResourcePackPacketAccessor;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @Final
    @Shadow
    private MinecraftServer server;

    @Final
    @Shadow
    private Connection connection;

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    public void handleCustomPayloadPre(ServerboundCustomPayloadPacket packet, CallbackInfo info) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            return;
        }

        ServerboundCustomPayloadPacketAccessor accessor = (ServerboundCustomPayloadPacketAccessor) packet;

        PayloadMessageEvent event = new PayloadMessageEvent(
            player,
            ((ConnectionAccessor) connection).getChannel(),
            accessor.getIdentifier(),
            accessor.getData()
        );

        server.dispatchEvent(event);

        if (event.isCancelled()) {
            info.cancel();
            return;
        }

        server.processPayload(event.getIdentifier(), event.getChannel(), event.getData());
    }

    @Redirect(
        method = "handleChat(Lnet/minecraft/server/network/TextFilter$FilteredText;)V",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/players/PlayerList.broadcastMessage(Lnet/minecraft/network/chat/Component;Ljava/util/function/Function;Lnet/minecraft/network/chat/ChatType;Ljava/util/UUID;)V"
        )
    )
    public void handleChat_broadcastMessage(
        PlayerList list,
        Component component,
        Function<ServerPlayer, Component> func,
        ChatType type,
        UUID uuid,
        TextFilter.FilteredText filtered
    ) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            list.broadcastMessage(component, func, type, uuid);
            return;
        }

        PlayerChatEvent event = new PlayerChatEvent(player, filtered.getFiltered(), new Chat(component, type, uuid));

        server.dispatchEvent(event);

        if (event.isCancelled()) return;

        Chat chat = event.getChat();
        list.broadcastMessage(chat.getComponent(), func, chat.getType(), chat.getUUID());
    }

    @Redirect(
        method = "handleCommand",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/commands/Commands.performCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I"
        )
    )
    public int handleCommand_performCommand(Commands commands, CommandSourceStack source, String rawCommand) {
        PancakeServer pancakeServer = PancakeServerService.getService().getServer();
        if (pancakeServer == null) {
            return commands.performCommand(source, rawCommand);
        }

        String command;
        if (rawCommand.startsWith("/")) {
            command = rawCommand.substring(1);
        } else {
            command = rawCommand;
        }

        PlayerCommandEvent event = new PlayerCommandEvent(player, command, source);

        pancakeServer.dispatchEvent(event);

        if (event.isCancelled()) return 0;

        if (event.getCommand().startsWith("/")) {
            command = event.getCommand().substring(1);
        } else {
            command = event.getCommand();
        }

        return server.getCommands().performCommand(event.getSource(), command);
    }

    @Inject(method = "handleResourcePackResponse", at = @At("HEAD"))
    public void handleResourcePackResponsePre(ServerboundResourcePackPacket packet, CallbackInfo info) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            return;
        }

        PlayerResourcePackStatusEvent event = new PlayerResourcePackStatusEvent(
            player,
            PlayerResourcePackStatusEvent.Status.fromPacketAction(((ServerboundResourcePackPacketAccessor) packet).getAction())
        );

        server.dispatchEvent(event);
    }

    @Redirect(method = "handlePlayerCommand", at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayer.setSprinting(Z)V"))
    public void handlePlayerCommand_setSprinting(ServerPlayer player, boolean sprint) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            player.setSprinting(sprint);
            return;
        }

        PlayerToggleSprintEvent event = new PlayerToggleSprintEvent(player, sprint);

        server.dispatchEvent(event);

        if (event.isCancelled()) return;

        player.setSprinting(event.isSprinting());
    }

    @Redirect(method = "handlePlayerCommand", at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayer.setShiftKeyDown(Z)V"))
    public void handlePlayerCommand_setShiftKeyDown(ServerPlayer player, boolean sneak) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            player.setShiftKeyDown(sneak);
            return;
        }

        PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(player, sneak);

        server.dispatchEvent(event);

        if (event.isCancelled()) return;

        player.setShiftKeyDown(event.isSneaking());
    }

    @Redirect(method = "handleAnimate", at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayer.swing(Lnet/minecraft/world/InteractionHand;)V"))
    public void handleAnimate_swing(ServerPlayer player, InteractionHand hand) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            player.swing(hand);
            return;
        }

        PlayerHandAnimateEvent event = new PlayerHandAnimateEvent(player, hand);

        if (event.isCancelled()) return;

        player.swing(event.getHand());
    }

    @Redirect(method = "handlePaddleBoat", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/vehicle/Boat.setPaddleState(ZZ)V"))
    public void handlePaddleBoat_setPaddleState(Boat boat, boolean left, boolean right) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            boat.setPaddleState(left, right);
            return;
        }

        PlayerPaddleBoatEvent event = new PlayerPaddleBoatEvent(player, left, right);

        if (event.isCancelled()) return;

        boat.setPaddleState(event.getLeft(), event.getRight());
    }

    @Redirect(
        method = "onDisconnect",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/players/PlayerList.broadcastMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/ChatType;Ljava/util/UUID;)V"
        )
    )
    public void onDisconnect_broadcastMessage(PlayerList list, Component component, ChatType type, UUID uuid) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            list.broadcastMessage(component, type, uuid);
            return;
        }

        PlayerLeaveChatEvent event = new PlayerLeaveChatEvent(player, new Chat(component, type, uuid));
        server.dispatchEvent(event);

        if (event.isCancelled()) return;
        
        Chat chat = event.getLeaveChat();
        list.broadcastMessage(chat.getComponent(), chat.getType(), chat.getUUID());
    }

    @Redirect(method = "handlePlayerAction", at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayer.drop(Z)Z"))
    public boolean handlePlayerAction_drop(ServerPlayer player, boolean dropAll) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            return player.drop(dropAll);
        }

        Inventory inventory = player.getInventory();

        PlayerDropItemEvent event = new PlayerDropItemEvent(
            player,
            inventory.getSelected(),
            dropAll,
            PlayerDropItemEvent.Source.SHORTCUT
        );
        server.dispatchEvent(event);

        if (event.isCancelled()) {
            // Fix player inventory so they don't think they lost item
            player.connection.send(
                new ClientboundContainerSetSlotPacket(
                    -2,
                    0,
                    inventory.selected,
                    inventory.getSelected()
                )
            );
            return false;
        }

        if (inventory.getSelected() != event.getDropItem()) {
            inventory.removeFromSelected(event.isDropAll());
            return player.drop(event.getDropItem(), false, true) != null;
        } else {
            return player.drop(event.isDropAll());
        }
    }

    @Redirect(
        method = "handleSetCreativeModeSlot",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/level/ServerPlayer.drop(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;"
        )
    )
    public ItemEntity handleSetCreativeModeSlot_drop(ServerPlayer player, ItemStack item, boolean dropAll) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            return player.drop(item, dropAll);
        }

        PlayerDropItemEvent event = new PlayerDropItemEvent(player, item, dropAll, PlayerDropItemEvent.Source.CREATIVE_INVENTORY);

        server.dispatchEvent(event);

        if (event.isCancelled()) {
            return null;
        }

        return player.drop(event.getDropItem(), event.isDropAll());
    }

    @Redirect(method = "handlePlayerInput", at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayer.setPlayerInput(FFZZ)V"))
    public void handlePlayerInput_setPlayerInput(ServerPlayer player, float xxa, float zza, boolean jumping, boolean sneaking) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            player.setPlayerInput(xxa, zza, jumping, sneaking);
            return;
        }

        PlayerVehicleInputEvent event = new PlayerVehicleInputEvent(player, xxa, zza, jumping, sneaking);

        server.dispatchEvent(event);
        if (event.isCancelled()) return;

        player.setPlayerInput(event.getXxa(), event.getZza(), event.isJumping(), event.isSneaking());
    }

    @Redirect(method = "handleMovePlayer", at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayer.jumpFromGround()V"))
    public void handleMovePlayer_jumpFromGround(ServerPlayer player) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            player.jumpFromGround();
            return;
        }

        PlayerJumpEvent event = new PlayerJumpEvent(player);

        server.dispatchEvent(event);

        if (event.isCancelled()) {
            return;
        }

        player.jumpFromGround();
    }

    @Redirect(method = "handleContainerClose", at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayer.doCloseContainer()V"))
    public void handleContainerClose_doCloseContainer(ServerPlayer player) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            player.doCloseContainer();
            return;
        }

        PlayerCloseMenuEvent event = new PlayerCloseMenuEvent(player, false);
        server.dispatchEvent(event);

        player.doCloseContainer();
    }

    @Overwrite
    public void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket packet) {
        PacketUtils.ensureRunningOnSameThread(packet, (ServerGamePacketListener) this, (ServerLevel) player.getLevel());

        StringReader reader = new StringReader(packet.getCommand());
        if (reader.canRead() && reader.peek() == '/') {
            reader.skip();
        }

        CommandSourceStack source = player.createCommandSourceStack();
        ParseResults<CommandSourceStack> parsed = server.getCommands().getDispatcher().parse(new StringReader(reader), source);

        CompletableFuture<Suggestions> future;
        PancakeServer pancakeServer = PancakeServerService.getService().getServer();
        if (pancakeServer != null) {
            PancakeCommandStack stack = pancakeServer.createCommandStack(source);

            future = BrigadierUtil.mergeSuggestionTasks(
                reader.getString(),
                Arrays.asList(
                    pancakeServer.getCompletionSuggestions(new StringReader(reader), stack),
                    server.getCommands().getDispatcher().getCompletionSuggestions(parsed)
                )
            );
        } else {
            future = server.getCommands().getDispatcher().getCompletionSuggestions(parsed);
        }

        future.thenAccept(suggestions -> {
            connection.send(new ClientboundCommandSuggestionsPacket(packet.getId(), suggestions));
        });
    }

}
