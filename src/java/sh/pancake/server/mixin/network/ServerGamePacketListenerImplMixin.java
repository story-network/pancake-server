/*
 * Created on Sat Aug 21 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.network;

import java.util.UUID;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.vehicle.Boat;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.impl.event.player.PayloadMessageEvent;
import sh.pancake.server.impl.event.player.PlayerChatEvent;
import sh.pancake.server.impl.event.player.PlayerCommandEvent;
import sh.pancake.server.impl.event.player.PlayerDisconnectEvent;
import sh.pancake.server.impl.event.player.PlayerDropItemEvent;
import sh.pancake.server.impl.event.player.PlayerHandAnimateEvent;
import sh.pancake.server.impl.event.player.PlayerJumpEvent;
import sh.pancake.server.impl.event.player.PlayerPaddleBoatEvent;
import sh.pancake.server.impl.event.player.PlayerResourcePackStatusEvent;
import sh.pancake.server.impl.event.player.PlayerToggleSneakEvent;
import sh.pancake.server.impl.event.player.PlayerToggleSprintEvent;
import sh.pancake.server.impl.event.player.PlayerVehicleInputEvent;
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

    @Shadow
    public abstract void handleChat(String text);

    @Shadow
    public abstract void filterTextPacket(String text, Consumer<String> consumer);

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

    @Redirect(method = "handleChat(Lnet/minecraft/network/protocol/game/ServerboundChatPacket;)V", at = @At(value = "INVOKE", target = "net/minecraft/server/network/ServerGamePacketListenerImpl.handleChat(Ljava/lang/String;)V"))
    public void handleChat_handleChat(ServerGamePacketListenerImpl impl, String text) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            handleChat(text);
            return;
        }

        if (text.startsWith("/")) {
            PlayerCommandEvent event = new PlayerCommandEvent(player, text.substring(1));

            server.dispatchEvent(event);

            if (event.isCancelled()) return;

            handleChat("/" + event.getCommand());
        }
    }

    @Redirect(method = "handleChat(Lnet/minecraft/network/protocol/game/ServerboundChatPacket;)V", at = @At(value = "INVOKE", target = "net/minecraft/server/network/ServerGamePacketListenerImpl.filterTextPacket(Ljava/lang/String;Ljava/util/function/Consumer;)V"))
    public void handleChat_filterTextPacket(ServerGamePacketListenerImpl impl, String text, Consumer<String> consumer) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            filterTextPacket(text, consumer);
            return;
        }

        PlayerChatEvent event = new PlayerChatEvent(player, text);

        server.dispatchEvent(event);

        if (event.isCancelled()) return;

        filterTextPacket(event.getMessage(), consumer);
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

    @Redirect(method = "onDisconnect", at = @At(value = "INVOKE", target = "net/minecraft/server/players/PlayerList.broadcastMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/ChatType;Ljava/util/UUID;)V"))
    public void onDisconnect_broadcastMessage(PlayerList list, Component component, ChatType type, UUID uuid) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            list.broadcastMessage(component, type, uuid);
            return;
        }

        PlayerDisconnectEvent event = new PlayerDisconnectEvent(player, component, type, uuid);

        if (event.isCancelled()) return;
        
        list.broadcastMessage(event.getLeaveText(), event.getType(), event.getBroadcastUUID());
    }

    @Redirect(method = "handlePlayerAction", at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayer.drop(Z)Z"))
    public boolean handlePlayerAction_drop(ServerPlayer player, boolean dropAll) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            return player.drop(dropAll);
        }

        PlayerDropItemEvent event = new PlayerDropItemEvent(player, dropAll, player.inventory.getSelected());
        server.dispatchEvent(event);

        if (event.isCancelled()) {
            // Fix player inventory so they don't think they lost item
            player.connection.send(new ClientboundContainerSetSlotPacket(
                -2,
                player.inventory.selected,
                player.inventory.getSelected())
            );
            return false;
        }

        return player.drop(event.isDropAll());
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


}
