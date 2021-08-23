/*
 * Created on Fri Aug 20 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network.payload;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;

/**
 * Provide payload IO
 */
public class PayloadChannel implements PayloadListener {

    private final ResourceLocation identifier;
    private final List<PayloadListener> listeners;

    public PayloadChannel(ResourceLocation identifier) {
        this.identifier = identifier;
        this.listeners = new ArrayList<>();
    }

    public ResourceLocation getIdentifier() {
        return identifier;
    }

    public List<PayloadListener> getListeners() {
        return listeners;
    }

    public void recvPayloadData(Channel channel, ByteBuf buf) {
        var iterator = listeners.iterator();
        
        while (iterator.hasNext()) {
            iterator.next().recvPayloadData(channel, buf.slice());
        }
    }

    public ChannelFuture send(Channel channel, InputStream stream) throws IOException {
        return send(channel, stream.readAllBytes());
    }

    public ChannelFuture send(Channel channel, byte[] data) {
        return send(channel, Unpooled.wrappedBuffer(data));
    }

    public ChannelFuture send(Channel channel, ByteBuf buf) {
        return send(channel, new FriendlyByteBuf(buf));
    }

    public ChannelFuture send(Channel channel, FriendlyByteBuf buf) {
        return channel.writeAndFlush(new ClientboundCustomPayloadPacket(identifier, buf));
    }

}
