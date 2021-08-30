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
import net.minecraft.world.level.block.state.BlockState;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.impl.event.entity.FallingBlockFormEvent;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin {

    @Shadow
    private BlockState blockState;

    @Shadow
    private boolean cancelDrop;

    @Shadow
    private boolean hurtEntities;

    @Shadow
    public CompoundTag blockData;
    
    @Redirect(method = "tick", at = @At(value = "FIELD", target = "net/minecraft/world/entity/item/FallingBlockEntity.cancelDrop", opcode = Opcodes.GETFIELD))
    public boolean tick_cancelDrop(FallingBlockEntity entity) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            return cancelDrop;
        }

        FallingBlockFormEvent event = new FallingBlockFormEvent(entity, blockState, blockData, hurtEntities);
        server.dispatchEvent(event);

        if (event.isCancelled()) {
            if (!cancelDrop) cancelDrop = true;
            return true;
        }

        if (event.getBlockState() != blockState) blockState = event.getBlockState();
        if (event.getBlockData() != blockData) blockData = event.getBlockData();

        if (event.isHurtEntities() != hurtEntities) hurtEntities = event.isHurtEntities();
        
        return cancelDrop;
    }

}
