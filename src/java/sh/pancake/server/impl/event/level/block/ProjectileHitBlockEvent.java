/*
 * Created on Sat Aug 28 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.level.block;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import sh.pancake.server.impl.event.CancellableEvent;
import sh.pancake.server.impl.event.entity.EntityEvent;

public class ProjectileHitBlockEvent extends CancellableEvent implements BlockEvent, EntityEvent<Projectile> {

    private final Level level;

    private Projectile entity;

    private BlockState blockState;
    private BlockHitResult result;

    public ProjectileHitBlockEvent(Level level, Projectile entity, BlockState blockState, BlockHitResult result) {
        this.level = level;

        this.entity = entity;

        this.blockState = blockState;
        this.result = result;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public Projectile getEntity() {
        return entity;
    }

    public void setEntity(Projectile entity) {
        this.entity = entity;
    }
    
    @Override
    public BlockState getBlockState() {
        return blockState;
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    public BlockHitResult getResult() {
        return result;
    }

    public void setResult(BlockHitResult result) {
        this.result = result;
    }
    
}
