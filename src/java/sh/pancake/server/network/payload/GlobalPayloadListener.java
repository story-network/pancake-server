package sh.pancake.server.network.payload;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface GlobalPayloadListener {
    
    void processPayload(ResourceLocation identifier, Channel channel, ByteBuf buf);

}
