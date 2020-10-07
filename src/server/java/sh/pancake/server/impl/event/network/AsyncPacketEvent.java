/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.network;

import javax.annotation.Nullable;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import sh.pancake.server.event.EventCancellable;

public class AsyncPacketEvent extends EventCancellable {

    private Packet<?> packet;
    private ServerPlayer sender;

    public AsyncPacketEvent(Packet<?> packet, @Nullable ServerPlayer sender) {
        this.packet = packet;
        this.sender = sender;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    @Nullable
    public ServerPlayer getSender() {
        return sender;
    }
    
}
