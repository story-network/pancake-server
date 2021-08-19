/*
 * Created on Thu Aug 12 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network.hook;

import java.io.Closeable;
import java.util.UUID;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

/**
 * Utility for adding custom ChannelHandler from Channel incoming from ChannelFuture.
 * 
 * Object must be closed so hook get detached from ChannelFuture.
 */
public class IncomingChannelHook implements Closeable {

    private String id;

    private final ChannelFuture future;
    private final HookInitializer initalizer;

    private final IncomingListener listener;

    public IncomingChannelHook(ChannelFuture future, IncomingListener listener) {
        this.id = UUID.randomUUID().toString();

        this.future = future;
        this.initalizer = new HookInitializer();

        this.listener = listener;

        updateHandler();
    }

    /**
     * Force reregister ChannelFuture hook
     */
    public void updateHandler() {
        ChannelPipeline pipeline = future.channel().pipeline();

        if (pipeline.names().contains(id)) {
            pipeline.remove(id);
        }

        pipeline.addFirst(id, initalizer);
    }

    /**
     * Add initialization hook to new incoming channel
     */
    private class HookInitializer extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            Channel channel = (Channel) msg;

            channel.pipeline().addFirst(new FinishHookInitializer());
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * Add hooker to last position.
     * This allows hooking after minecraft added necessary handlers.
     */
    private class FinishHookInitializer extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel channel) throws Exception {
            channel.pipeline().addLast(new NetworkHooker());
        }
    }

    private class NetworkHooker extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel channel) throws Exception {
            channel.eventLoop().submit(() -> {
                try {
                    listener.handleConnection(channel);
                } catch (Exception e) {
                    throw new RuntimeException("Exception while handling connection", e);
                }
            });
        }

    }

    @Override
    public void close() {
        ChannelPipeline pipeline = future.channel().pipeline();

        if (pipeline.names().contains(id)) {
            pipeline.remove(id);
        }
    }
    
}
