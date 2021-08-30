/*
 * Created on Mon Aug 30 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import sh.pancake.server.impl.event.CancellableResultEvent;
import sh.pancake.server.impl.event.level.ServerLevelEvent;
import sh.pancake.server.impl.player.BlockActionInfo;
import sh.pancake.server.impl.player.EntityInteractInfo;

public class PlayerUseItemEvent extends CancellableResultEvent<InteractionResult> implements ServerPlayerEvent, ServerLevelEvent {

    private final ServerPlayer player;
    private final ServerLevel level;

    private ItemStack item;
    private InteractionHand hand;

    private final BlockActionInfo placeInfo;
    private final EntityInteractInfo entityInteractInfo;

    public PlayerUseItemEvent(
        ServerPlayer player,
        ServerLevel level,
        ItemStack item,
        InteractionHand hand,
        @Nullable BlockActionInfo placeInfo,
        @Nullable EntityInteractInfo entityInteractInfo
    ) {
        this.player = player;
        this.level = level;
        this.item = item;
        this.hand = hand;

        this.placeInfo = placeInfo;
        this.entityInteractInfo = entityInteractInfo;
    }

    public PlayerUseItemEvent(
        ServerPlayer player,
        ServerLevel level,
        ItemStack item,
        InteractionHand hand
    ) {
        this(player, level, item, hand, null, null);
    }

    public PlayerUseItemEvent(
        ServerPlayer player,
        ServerLevel level,
        ItemStack item,
        InteractionHand hand,
        @Nullable BlockActionInfo blockInfo
    ) {
        this(player, level, item, hand, blockInfo, null);
    }

    public PlayerUseItemEvent(
        ServerPlayer player,
        ServerLevel level,
        ItemStack item,
        InteractionHand hand,
        @Nullable EntityInteractInfo entityInteractInfo
    ) {
        this(player, level, item, hand, null, entityInteractInfo);
    }

    @Override
    public ServerPlayer getPlayer() {
        return player;
    }

    @Override
    public ServerLevel getLevel() {
        return level;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public void setHand(InteractionHand hand) {
        this.hand = hand;
    }
    
    @Nullable
    public BlockActionInfo getPlaceInfo() {
        return placeInfo;
    }

    @Nullable
    public EntityInteractInfo getEntityInteractInfo() {
        return entityInteractInfo;
    }

}
