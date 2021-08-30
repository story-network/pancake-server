/*
 * Created on Mon Aug 30 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.fix.floating_vehicle;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplFix {

    @Shadow
    public ServerPlayer player;

    @Shadow
    private boolean clientIsFloating;
    
    @Redirect(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/server/network/ServerGamePacketListenerImpl.clientIsFloating",
            opcode = Opcodes.GETFIELD,
            ordinal = 0
        )
    )
    public boolean tick_clientIsFloating(ServerGamePacketListenerImpl impl) {
        return clientIsFloating && player.getRootVehicle() == player;
    }

}
