/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.network;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.protocol.PacketFlow;
import sh.pancake.launcher.PancakeLauncher;
import sh.pancake.server.PancakeServer;

@Mixin(PacketDecoder.class)
public abstract class PacketDecoderMixin {

    @Shadow
    private PacketFlow flow;
    
    @Inject(method = "decode", at = @At("HEAD"))
    public void onDecode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> packetList, CallbackInfo info) throws Exception {
        // Here we will impl our decoder
        info.cancel();

        PancakeServer server = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        server.getNetworkManager().handleInPacket(ctx, buf, packetList, flow);
    }
}
