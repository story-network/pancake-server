/*
 * Created on Sat Oct 10 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;

@Pseudo
@Mixin(ServerboundResourcePackPacket.class)
public interface ServerboundResourcePackPacketAccessor {

    @Accessor("action")
    ServerboundResourcePackPacket.Action getAction();

}
