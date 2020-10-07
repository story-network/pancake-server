/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.network;

import javax.annotation.Nullable;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import sh.pancake.server.impl.network.DefaultPacketSerializer;
import sh.pancake.server.impl.network.IPacketSerializer;

/* 
 *
 * AsyncPacketOutEvent
 * Called when packet send to client from server
 * 
 */
public class AsyncPacketOutEvent extends AsyncPacketEvent {

    private IPacketSerializer serializer;

    public AsyncPacketOutEvent(Packet<?> packet, @Nullable ServerPlayer sender, IPacketSerializer serializer) {
        super(packet, sender);

        this.serializer = serializer;
    }

    public AsyncPacketOutEvent(Packet<?> packet, @Nullable ServerPlayer sender) {
        this(packet, sender, new DefaultPacketSerializer());
    }

    public IPacketSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(IPacketSerializer serializer) {
        this.serializer = serializer;
    }
    
}
