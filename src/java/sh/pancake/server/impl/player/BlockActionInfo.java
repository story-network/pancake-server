/*
 * Created on Mon Aug 30 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.player;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class BlockActionInfo {

    private BlockPos position;
    private Direction direction;

    public BlockActionInfo(BlockPos position, Direction direction) {
        this.position = position;
        this.direction = direction;
    }

    public BlockPos getPosition() {
        return position;
    }

    public void setPosition(BlockPos position) {
        this.position = position;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

}
