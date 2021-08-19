/*
 * Created on Wed Aug 18 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.concurrent.network;

import io.netty.channel.Channel;
import sh.pancake.server.network.PacketContainer;

public class PacketOutEvent extends PacketEvent {

    public PacketOutEvent(Channel channel, PacketContainer packet) {
        super(channel, packet);
    }
    
}
