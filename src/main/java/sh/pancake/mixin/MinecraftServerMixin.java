/*
 * Created on Wed Sep 30 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.progress.ChunkProgressListener;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    
    @Inject(method = "prepareLevels", at = @At("HEAD"))
    private void onPrepareLevels(ChunkProgressListener var1, CallbackInfo info) {
        System.out.println("Hello world!");
    }

}
