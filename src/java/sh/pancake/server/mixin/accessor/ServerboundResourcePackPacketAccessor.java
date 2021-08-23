/*
 * Created on Mon Aug 23 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;

@Mixin(ServerboundResourcePackPacket.class)
public interface ServerboundResourcePackPacketAccessor {

    @Accessor("action")
    ServerboundResourcePackPacket.Action getAction();
    
}
