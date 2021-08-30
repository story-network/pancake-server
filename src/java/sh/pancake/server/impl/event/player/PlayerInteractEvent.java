/*
 * Created on Mon Aug 30 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import sh.pancake.server.impl.player.BlockActionInfo;
import sh.pancake.server.impl.player.EntityInteractInfo;

public class PlayerInteractEvent extends ServerPlayerEventImpl {

    private InteractionHand hand;

    private final BlockActionInfo breakInfo;
    private final EntityInteractInfo entityInteractInfo;

    public PlayerInteractEvent(
        ServerPlayer player,
        InteractionHand hand,
        @Nullable BlockActionInfo breakInfo,
        @Nullable EntityInteractInfo entityInteractInfo
    ) {
        super(player);

        this.hand = hand;

        this.breakInfo = breakInfo;
        this.entityInteractInfo = entityInteractInfo;
    }

    public PlayerInteractEvent(
        ServerPlayer player,
        InteractionHand hand,
        @Nullable BlockActionInfo breakInfo
    ) {
        this(player, hand, breakInfo, null);
    }

    public PlayerInteractEvent(
        ServerPlayer player,
        InteractionHand hand,
        @Nullable EntityInteractInfo entityInteractInfo
    ) {
        this(player, hand, null, entityInteractInfo);
    }

    public PlayerInteractEvent(
        ServerPlayer player,
        InteractionHand hand
    ) {
        this(player, hand, null, null);
    }

    public InteractionHand getHand() {
        return hand;
    }

    public void setHand(InteractionHand hand) {
        this.hand = hand;
    }

    @Nullable
    public BlockActionInfo getBreakInfo() {
        return breakInfo;
    }

    @Nullable
    public EntityInteractInfo getEntityInteractInfo() {
        return entityInteractInfo;
    }

}
