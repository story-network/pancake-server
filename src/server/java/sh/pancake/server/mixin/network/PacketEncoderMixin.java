/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import sh.pancake.launcher.PancakeLauncher;
import sh.pancake.server.PancakeServer;

@Mixin(PacketEncoder.class)
public abstract class PacketEncoderMixin {

    @Shadow
    private PacketFlow flow;
    
    @Inject(method = "encode", at = @At("HEAD"))
    public void onEncode(ChannelHandlerContext ctx, Packet<?> packet, ByteBuf buf, CallbackInfo info) throws Exception {
        // Here we will impl our encoder
        info.cancel();

        PancakeServer server = (PancakeServer) PancakeLauncher.getLauncher().getServer();
        server.getNetworkManager().handleOutPacket(ctx, packet, buf, flow);
    }

}
