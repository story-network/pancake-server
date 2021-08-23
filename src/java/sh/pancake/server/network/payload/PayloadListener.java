package sh.pancake.server.network.payload;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

@FunctionalInterface
public interface PayloadListener {

    void recvPayloadData(Channel channel, ByteBuf buf);

}
