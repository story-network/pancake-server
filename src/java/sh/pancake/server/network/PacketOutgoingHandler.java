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
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelHandler.Sharable;
import sh.pancake.server.event.EventDispatcher;
import sh.pancake.server.impl.event.network.PacketOutEvent;

@Sharable
public class PacketOutgoingHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger LOGGER = LogManager.getLogger();

    private final EventDispatcher dispatcher;

    public PacketOutgoingHandler(EventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try {
            if (msg instanceof ByteBuf) {
                ByteBuf buf = (ByteBuf) msg;
                PacketContainer container = PacketContainer.readPacketFrom(buf.slice());

                PacketOutEvent event = new PacketOutEvent(ctx.channel(), container);
                dispatcher.dispatchEvent(event);

                if (event.isCancelled()) {
                    buf.release();
                    return;
                }

                buf.clear();
                container.writePacketTo(buf);
            }
        } catch (Exception e) {
            LOGGER.error("Error while hooking outgoing packet " + msg, e);
        }

        super.write(ctx, msg, promise);
    }

}
