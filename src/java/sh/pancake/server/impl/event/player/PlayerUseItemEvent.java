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

public class PlayerUseItemEvent extends PlayerInteractEventBase {

    public PlayerUseItemEvent(
        ServerPlayer player,
        InteractionHand hand
    ) {
        super(player, hand);
    }

    public PlayerUseItemEvent(
        ServerPlayer player,
        InteractionHand hand,
        @Nullable BlockActionInfo blockInfo
    ) {
        super(player, hand, blockInfo);
    }

    public PlayerUseItemEvent(
        ServerPlayer player,
        InteractionHand hand,
        @Nullable EntityInteractInfo entityInteractInfo
    ) {
        super(player, hand, entityInteractInfo);
    }

}
