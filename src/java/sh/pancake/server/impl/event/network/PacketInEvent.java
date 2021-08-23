/*
 * Created on Wed Aug 18 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.network;

import io.netty.channel.Channel;
import sh.pancake.server.network.PacketContainer;

public class PacketInEvent extends PacketEvent {

    public PacketInEvent(Channel channel, PacketContainer packet) {
        super(channel, packet);
    }
    
}
