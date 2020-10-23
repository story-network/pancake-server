/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network;

import javax.annotation.Nullable;

import io.netty.channel.Channel;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;

public abstract class AsyncPacketEvent implements IPacketEvent {

    private Packet<?> packet;

    private Channel channel;
    private ServerPlayer sender;

    private boolean cancelled;

    public AsyncPacketEvent(Channel channel, Packet<?> packet, @Nullable ServerPlayer sender) {
        this.channel = channel;
        this.packet = packet;
        this.sender = sender;

        this.cancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean flag) {
        if (this.cancelled == flag) return;
        this.cancelled = flag;
    }

    public Channel getChannel() {
        return channel;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public <P extends Packet<?>>P getPacketAs(Class<P> packetClass) {
        if (packetClass.isInstance(packet)) return (P) packet;

        return null;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    @Nullable
    public ServerPlayer getSender() {
        return sender;
    }
    
}
