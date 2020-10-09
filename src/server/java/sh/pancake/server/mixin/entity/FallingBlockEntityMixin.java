/*
 * Created on Fri Oct 09 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.entity;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import sh.pancake.launcher.PancakeLauncher;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.impl.event.entity.FallingBlockFormEvent;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin {

    @Shadow
    private BlockState blockState;
    
    @Shadow
    public boolean dropItem;

    @Shadow
    private boolean cancelDrop;

    @Shadow
    private boolean hurtEntities;

    @Shadow
    private int fallDamageMax;

    @Shadow
    private float fallDamageAmount;

    @Shadow
    public CompoundTag blockData;
    
    @Redirect(method = "tick", at = @At(value = "FIELD", target = "net/minecraft/world/entity/item/FallingBlockEntity.cancelDrop:Z", opcode = Opcodes.GETFIELD))
    public boolean onFallingBlockForm(FallingBlockEntity entity) {
        PancakeServer pancakeServer = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        FallingBlockFormEvent event = new FallingBlockFormEvent(entity, blockState, dropItem, cancelDrop, hurtEntities, fallDamageAmount, fallDamageMax, blockData);

        pancakeServer.getEventManager().callEvent(event);

        if (event.isCancelled()) return true;

        if (event.getBlockState() != blockState) blockState = event.getBlockState();

        if (event.isDropItem() != dropItem) dropItem = event.isDropItem();

        if (event.isHurtEntities() != hurtEntities) hurtEntities = event.isHurtEntities();

        if (event.getFallDamage() != fallDamageAmount) fallDamageAmount = event.getFallDamage();
        if (event.getFallDamageMax() != fallDamageMax) fallDamageMax = event.getFallDamageMax();

        if (event.getBlockData() != blockData) blockData = event.getBlockData();
        
        return cancelDrop = event.isDropCancelled();
    }

}
