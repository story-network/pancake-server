/*
 * Created on Wed Aug 18 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.network;

import io.netty.channel.Channel;
import sh.pancake.server.impl.event.CancellableEvent;
import sh.pancake.server.network.PacketContainer;

public class PacketEvent extends CancellableEvent {

    private final Channel channel;
    private final PacketContainer packet;

    public PacketEvent(Channel channel, PacketContainer packet) {
        this.channel = channel;
        this.packet = packet;
    }

    public PacketContainer getPacket() {
        return packet;
    }

    public Channel getChannel() {
        return channel;
    }

}
