/*
 * Created on Mon Aug 30 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.entity;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FallingBlockFormEvent extends EntityEventImpl<FallingBlockEntity> {

    private BlockState blockState;

    private CompoundTag blockData;

    private boolean hurtEntities;

    public FallingBlockFormEvent(
        FallingBlockEntity entity,
        BlockState blockState,
        @Nullable CompoundTag blockData,
        boolean hurtEntities
    ) {
        super(entity);

        this.blockState = blockState;
        this.blockData = blockData;

        this.hurtEntities = hurtEntities;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    @Nullable
    public CompoundTag getBlockData() {
        return blockData;
    }

    public void setBlockData(@Nullable CompoundTag blockData) {
        this.blockData = blockData;
    }

    public boolean isHurtEntities() {
        return hurtEntities;
    }

    public void setHurtEntities(boolean hurtEntities) {
        this.hurtEntities = hurtEntities;
    }
    
}
