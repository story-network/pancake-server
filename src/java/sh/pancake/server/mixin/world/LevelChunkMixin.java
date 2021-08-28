/*
 * Created on Sat Aug 28 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.world;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {
    
    
    /*
    @Redirect(
        method = "setBlockState",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase.onPlace(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V"
        )
    )
    public void setBlockState_onPlace(BlockBehaviour.BlockStateBase base, Level level, BlockPos pos, BlockState state, boolean idk) {
        base.onPlace(level, pos, state, idk);
    }

    @Redirect(
        method = "setBlockState",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase.onRemove(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V"
        )
    )
    public void setBlockState_onRemove(BlockBehaviour.BlockStateBase base, Level level, BlockPos pos, BlockState state, boolean idk) {
        base.onRemove(level, pos, state, idk);
    }
    */

}
