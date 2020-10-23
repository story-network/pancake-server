/*
 * Created on Fri Oct 23 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

@Pseudo
@Mixin(ServerboundMovePlayerPacket.class)
public interface ServerboundMovePlayerPacketAccessor {
    
    @Accessor("hasPos")
    boolean hasPos();

    @Accessor("hasRot")
    boolean hasRot();

}
