/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.game;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import sh.pancake.launcher.PancakeLauncher;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.impl.event.player.PlayerBlockActionEvent;
import sh.pancake.server.impl.event.player.PlayerChatEvent;
import sh.pancake.server.impl.event.player.PlayerDropItemEvent;
import sh.pancake.server.impl.event.player.PlayerFallFlyEvent;
import sh.pancake.server.impl.event.player.PlayerPreCommandEvent;
import sh.pancake.server.impl.event.player.PlayerToggleCrouchEvent;
import sh.pancake.server.impl.event.player.PlayerToggleSprintEvent;
import sh.pancake.server.impl.event.player.PlayerVehicleInputEvent;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @Shadow
    private MinecraftServer server;
    
    @Redirect(method = "handlePlayerInput", at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayer.setPlayerInput(FFZZ)V"))
    public void onPlayerInput(ServerPlayer player, float xxa, float zza, boolean jumping, boolean sneaking) {
        PancakeServer pancakeServer = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        PlayerVehicleInputEvent event = new PlayerVehicleInputEvent(player, xxa, zza, jumping, sneaking);

        pancakeServer.getEventManager().callEvent(event);

        if (event.isCancelled()) return;

        player.setPlayerInput(event.getXxa(), event.getZza(), event.isJumping(), event.isSneaking());
    }

    @Shadow
    private void handleCommand(String command) {}

    @Redirect(method = "handleChat", at = @At(value = "INVOKE", target = "net/minecraft/server/network/ServerGamePacketListenerImpl.handleCommand(Ljava/lang/String;)V"))
    public void onPlayerCommandChat(ServerGamePacketListenerImpl impl, String command) {
        PancakeServer pancakeServer = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        PlayerPreCommandEvent event = new PlayerPreCommandEvent(player, command);

        pancakeServer.getEventManager().callEvent(event);

        if (event.isCancelled()) return;

        handleCommand(event.getCommand());
    }

    @Redirect(method = "handleChat", at = @At(value = "INVOKE", target = "net/minecraft/server/players/PlayerList.broadcastMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/ChatType;Ljava/util/UUID;)V"))
    public void onPlayerChat(PlayerList playerList, Component component, ChatType type, UUID uuid) {
        PancakeServer pancakeServer = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        PlayerChatEvent event = new PlayerChatEvent(player, component, type, uuid);

        pancakeServer.getEventManager().callEvent(event);

        if (event.isCancelled()) return;

        server.getPlayerList().broadcastMessage(event.getComponent(), event.getChatType(), event.getSenderUUID());
    }

    @Redirect(method = "handlePlayerCommand", at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayer.setSprinting(Z)V"))
    public void onPlayerSprint(ServerPlayer player, boolean sprint) {
        PancakeServer pancakeServer = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        PlayerToggleSprintEvent event = new PlayerToggleSprintEvent(player, sprint);

        pancakeServer.getEventManager().callEvent(event);

        if (event.isCancelled()) return;

        player.setSprinting(event.shouldSprint());
    }

    @Redirect(method = "handlePlayerCommand", at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayer.setShiftKeyDown(Z)V"))
    public void onPlayerCrouch(ServerPlayer player, boolean sneak) {
        PancakeServer pancakeServer = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        PlayerToggleCrouchEvent event = new PlayerToggleCrouchEvent(player, sneak);

        pancakeServer.getEventManager().callEvent(event);

        if (event.isCancelled()) return;

        player.setShiftKeyDown(event.shouldCrouch());
    }

    @Redirect(method = "handlePlayerCommand", at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayer.tryToStartFallFlying()Z"))
    public boolean onPlayerFallFly(ServerPlayer player) {
        PancakeServer pancakeServer = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        PlayerFallFlyEvent event = new PlayerFallFlyEvent(player, !player.isFallFlying());

        pancakeServer.getEventManager().callEvent(event);

        // Return true with nothing so it does nothing
        if (event.isCancelled()) return true;

        if (event.shouldFallFly()) return player.tryToStartFallFlying();

        // Return false so it will stop fall flying
        return false;
    }

    @Redirect(method = "handlePlayerAction", at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayerGameMode.handleBlockBreakAction(Lnet/minecraft/core/BlockPos;Lnet/minecraft/network/protocol/game/ServerboundPlayerActionPacket$Action;Lnet/minecraft/core/Direction;I)V"))
    public void onHandleBlockBreakAction(ServerPlayerGameMode gamemode, BlockPos position, ServerboundPlayerActionPacket.Action action, Direction direction, int maxBuildHeight) {
        PancakeServer pancakeServer = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        PlayerBlockActionEvent event = new PlayerBlockActionEvent(player, action, position, direction, maxBuildHeight);

        pancakeServer.getEventManager().callEvent(event);

        if (event.isCancelled()) {
            // We should notify action cancelled so client undo the action
            player.connection.send(new ClientboundBlockBreakAckPacket(position, gamemode.level.getBlockState(position), action, false, "Cancelled"));
            return;
        }

        gamemode.handleBlockBreakAction(event.getPosition(), event.getAction(), event.getActionDirection(), event.getMaxBuildYLimit());
    }

    @Redirect(method = "handlePlayerAction", at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayer.drop(Z)Z"))
    public boolean onDropItem(ServerPlayer player, boolean dropAll) {
        PancakeServer pancakeServer = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        PlayerDropItemEvent event = new PlayerDropItemEvent(player, dropAll, player.inventory.getSelected());

        pancakeServer.getEventManager().callEvent(event);

        if (event.isCancelled()) {
            // Fix player inventory so they don't think they lost item
            player.connection.send(new ClientboundContainerSetSlotPacket(-2, player.inventory.selected, player.inventory.getSelected()));
            return false;
        }

        return player.drop(event.isDropAll());
    }

    @Redirect(method = "handleSetCreativeModeSlot", at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayer.drop(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;"))
    public ItemEntity onCreativeDrop(ServerPlayer player, ItemStack item, boolean dropAll) {
        PancakeServer pancakeServer = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        PlayerDropItemEvent event = new PlayerDropItemEvent(player, dropAll, item);

        pancakeServer.getEventManager().callEvent(event);

        if (event.isCancelled()) return null;

        return player.drop(event.getDropItem(), event.isDropAll());
    }

}
