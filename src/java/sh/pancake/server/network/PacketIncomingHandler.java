/*
 * Created on Wed Aug 18 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import sh.pancake.server.event.EventDispatcher;
import sh.pancake.server.impl.event.concurrent.network.PacketInEvent;

@Sharable
public class PacketIncomingHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LogManager.getLogger();

    private final EventDispatcher dispatcher;

    public PacketIncomingHandler(EventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof ByteBuf) {
                ByteBuf buf = (ByteBuf) msg;
                PacketContainer container = PacketContainer.readPacketFrom(buf.slice());

                PacketInEvent event = new PacketInEvent(ctx.channel(), container);
                dispatcher.dispatchEvent(event);

                if (event.isCancelled()) {
                    buf.release();
                    return;
                }

                buf.clear();
                container.writePacketTo(buf);
            }
        } catch (Exception e) {
            LOGGER.error("Error while hooking incoming packet " + msg, e);
        }

        super.channelRead(ctx, msg);
    }

}
