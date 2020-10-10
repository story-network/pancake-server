/*
 * Created on Sat Oct 10 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.server.level.ServerPlayer;

public class PlayerBlockActionEvent extends PlayerActionEvent {

    private BlockPos position;
    private Direction actionDirection;
    private int maxBuildYLimit;

    public PlayerBlockActionEvent(ServerPlayer player, Action action, BlockPos position, Direction actionDirection, int maxBuildYLimit) {
        super(player, action);

        this.position = position;
        this.actionDirection = actionDirection;
        this.maxBuildYLimit = maxBuildYLimit;
    }

    public BlockPos getPosition() {
        return position;
    }

    public void setPosition(BlockPos position) {
        this.position = position;
    }

    public Direction getActionDirection() {
        return actionDirection;
    }

    public void setActionDirection(Direction actionDirection) {
        this.actionDirection = actionDirection;
    }

    public int getMaxBuildYLimit() {
        return maxBuildYLimit;
    }

    public void setMaxBuildYLimit(int maxBuildYLimit) {
        this.maxBuildYLimit = maxBuildYLimit;
    }
    
}
