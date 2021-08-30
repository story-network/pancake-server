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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.command.BrigadierUtil;
import sh.pancake.server.command.PancakeCommandStack;
import sh.pancake.server.impl.event.player.PayloadMessageEvent;
import sh.pancake.server.impl.event.player.PlayerChatEvent;
import sh.pancake.server.impl.event.player.PlayerCloseMenuEvent;
import sh.pancake.server.impl.event.player.PlayerLeaveChatEvent;
import sh.pancake.server.impl.event.player.PlayerMenuButtonClickEvent;
import sh.pancake.server.impl.event.player.PlayerMoveEvent;
import sh.pancake.server.impl.event.player.PlayerDropItemEvent;
import sh.pancake.server.impl.event.player.PlayerUseItemEvent;
import sh.pancake.server.impl.event.player.PlayerJumpEvent;
import sh.pancake.server.impl.event.player.PlayerPaddleBoatEvent;
import sh.pancake.server.impl.event.player.PlayerResourcePackStatusEvent;
import sh.pancake.server.impl.event.player.PlayerInteractEvent;
import sh.pancake.server.impl.event.player.PlayerToggleSneakEvent;
import sh.pancake.server.impl.event.player.PlayerToggleSprintEvent;
import sh.pancake.server.impl.event.player.PlayerVehicleInputEvent;
import sh.pancake.server.impl.network.Chat;
import sh.pancake.server.impl.player.BlockActionInfo;
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
    private double lastGoodX;

    @Shadow
    private double lastGoodY;

    @Shadow
    private double lastGoodZ;

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

        PlayerInteractEvent event = new PlayerInteractEvent(player, hand);

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

    @Redirect(
        method = "handleContainerButtonClick",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/world/inventory/AbstractContainerMenu.clickMenuButton(Lnet/minecraft/world/entity/player/Player;I)Z"
        )
    )
    public boolean handleContainerButtonClick_clickMenuButton(AbstractContainerMenu menu, Player player, int buttonId) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            return menu.clickMenuButton(player, buttonId);
        }

        PlayerMenuButtonClickEvent event = new PlayerMenuButtonClickEvent((ServerPlayer) player, menu, buttonId);
        server.dispatchEvent(event);

        if (event.isCancelled()) return false;

        return menu.clickMenuButton(player, event.getButtonId());
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

    @Redirect(
        method = "handleUseItem",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/level/ServerPlayerGameMode.useItem(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"
        )
    )
    public InteractionResult handleUseItem_useItem(
        ServerPlayerGameMode mode,
        ServerPlayer player,
        Level level,
        ItemStack item,
        InteractionHand hand
    ) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            return mode.useItem(player, level, item, hand);
        }

        PlayerUseItemEvent event = new PlayerUseItemEvent(player, (ServerLevel) level, item, hand);
        server.dispatchEvent(event);

        if (event.isCancelled()) {
            return event.getCancelledResult();
        }

        return mode.useItem(player, level, event.getItem(), event.getHand());
    }

    @Redirect(
        method = "handleUseItemOn",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/level/ServerPlayerGameMode.useItemOn(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"
        )
    )
    public InteractionResult handleUseItemOn_useItemOn(
        ServerPlayerGameMode mode,
        ServerPlayer player,
        Level level,
        ItemStack item,
        InteractionHand hand,
        BlockHitResult hitResult
    ) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            return mode.useItemOn(player, level, item, hand, hitResult);
        }

        PlayerUseItemEvent event = new PlayerUseItemEvent(player, (ServerLevel) level, item, hand, new BlockActionInfo(hitResult.getBlockPos(), hitResult.getDirection()));
        server.dispatchEvent(event);

        if (event.isCancelled()) {
            return event.getCancelledResult();
        }

        if (!hitResult.getBlockPos().equals(event.getPlaceInfo().getPosition())) {
            hitResult = hitResult.withPosition(event.getPlaceInfo().getPosition());
        }

        if (!hitResult.getDirection().equals(event.getPlaceInfo().getDirection())) {
            hitResult = hitResult.withDirection(event.getPlaceInfo().getDirection());
        }

        return mode.useItemOn(player, level, event.getItem(), event.getHand(), hitResult);
    }

    /*
    @Redirect(
        method = "handleInteract",
        at = @At(
            value = "INVOKE"
        )
    )
    public void handleInteract_TODO() {

    }
    */

    @Redirect(
        method = "handlePlayerAction",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/level/ServerPlayerGameMode.handleBlockBreakAction(Lnet/minecraft/core/BlockPos;Lnet/minecraft/network/protocol/game/ServerboundPlayerActionPacket$Action;Lnet/minecraft/core/Direction;I)V"
        )
    )
    public void handlePlayerAction_handleBlockBreakAction(
        ServerPlayerGameMode mode,
        BlockPos pos,
        ServerboundPlayerActionPacket.Action action,
        Direction direction,
        int maxHeight,
        ServerboundPlayerActionPacket packet
    ) {
        if (action != ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
            mode.handleBlockBreakAction(pos, action, direction, maxHeight);
            return;
        }

        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            mode.handleBlockBreakAction(pos, action, direction, maxHeight);
            return;
        }

        PlayerInteractEvent event = new PlayerInteractEvent(player, player.swingingArm, new BlockActionInfo(pos, direction));
        server.dispatchEvent(event);
        if (event.isCancelled()) {
            player.connection.send(new ClientboundBlockUpdatePacket(player.getLevel(), pos));
            player.connection.send(new ClientboundBlockUpdatePacket(player.getLevel(), pos.relative(direction)));
            return;
        }

        BlockActionInfo info = event.getBreakInfo();

        mode.handleBlockBreakAction(info.getPosition(), action, info.getDirection(), maxHeight);
    }

    @Redirect(
        method = "handleMovePlayer",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/world/entity/Entity.move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"
        )
    )
    public void handleMovePlayer_move(Entity entity, MoverType type, Vec3 delta) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            entity.move(type, delta);
            return;
        }

        PlayerMoveEvent event = new PlayerMoveEvent(player, lastGoodX, lastGoodY, lastGoodZ, delta);
        server.dispatchEvent(event);
        if (event.isCancelled()) return;

        entity.move(type, event.getVec());
    }

}
