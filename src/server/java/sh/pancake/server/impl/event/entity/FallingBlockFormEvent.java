/*
 * Created on Fri Oct 09 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.entity;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FallingBlockFormEvent extends EntityEvent<FallingBlockEntity> {

    private BlockState blockState;
    private boolean dropItem;

    private boolean dropCancelled;

    private boolean hurtEntities;

    private float fallDamage;
    private int fallDamageMax;

    private CompoundTag blockData;

    public FallingBlockFormEvent(FallingBlockEntity entity, BlockState blockState, boolean dropItem, boolean dropCancelled, boolean hurtEntities, float fallDamage, int fallDamageMax, @Nullable CompoundTag blockData) {
        super(entity);

        this.blockState = blockState;
        this.dropItem = dropItem;

        this.hurtEntities = hurtEntities;

        this.fallDamage = fallDamage;
        this.fallDamageMax = fallDamageMax;

        this.blockData = blockData;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    public boolean isDropItem() {
        return dropItem;
    }

    public void setDropItem(boolean dropItem) {
        this.dropItem = dropItem;
    }

    public boolean isDropCancelled() {
        return dropCancelled;
    }

    public void setDropCancelled(boolean dropCancelled) {
        this.dropCancelled = dropCancelled;
    }

    public boolean isHurtEntities() {
        return hurtEntities;
    }

    public void setHurtEntities(boolean hurtEntities) {
        this.hurtEntities = hurtEntities;
    }

    public float getFallDamage() {
        return fallDamage;
    }

    public void setFallDamage(float fallDamage) {
        this.fallDamage = fallDamage;
    }

    public int getFallDamageMax() {
        return fallDamageMax;
    }

    public void setFallDamageMax(int fallDamageMax) {
        this.fallDamageMax = fallDamageMax;
    }

    public void setBlockData(CompoundTag blockData) {
        this.blockData = blockData;
    }

    @Nullable
    public CompoundTag getBlockData() {
        return blockData;
    }
    
}
