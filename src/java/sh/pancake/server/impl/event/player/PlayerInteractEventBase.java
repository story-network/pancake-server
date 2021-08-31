/*
 * Created on Mon Aug 30 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import sh.pancake.server.impl.event.CancellableResultEvent;
import sh.pancake.server.impl.player.BlockActionInfo;
import sh.pancake.server.impl.player.EntityInteractInfo;

public abstract class PlayerInteractEventBase extends CancellableResultEvent<InteractionResult> implements ServerPlayerEvent {

    private final ServerPlayer player;

    private InteractionHand hand;

    private final BlockActionInfo blockInfo;
    private final EntityInteractInfo entityInteractInfo;

    public PlayerInteractEventBase(
        ServerPlayer player,
        InteractionHand hand,
        @Nullable BlockActionInfo blockInfo,
        @Nullable EntityInteractInfo entityInteractInfo
    ) {
        this.player = player;
        this.hand = hand;

        this.blockInfo = blockInfo;
        this.entityInteractInfo = entityInteractInfo;
    }

    public PlayerInteractEventBase(
        ServerPlayer player,
        InteractionHand hand
    ) {
        this(player, hand, null, null);
    }

    public PlayerInteractEventBase(
        ServerPlayer player,
        InteractionHand hand,
        @Nullable BlockActionInfo blockInfo
    ) {
        this(player, hand, blockInfo, null);
    }

    public PlayerInteractEventBase(
        ServerPlayer player,
        InteractionHand hand,
        @Nullable EntityInteractInfo entityInteractInfo
    ) {
        this(player, hand, null, entityInteractInfo);
    }

    @Override
    public ServerPlayer getPlayer() {
        return player;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public void setHand(InteractionHand hand) {
        this.hand = hand;
    }
    
    @Nullable
    public BlockActionInfo getBlockInfo() {
        return blockInfo;
    }

    @Nullable
    public EntityInteractInfo getEntityInteractInfo() {
        return entityInteractInfo;
    }

    public void cancel() {
        cancel(InteractionResult.PASS);
    }

}
