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

public class AsyncPacketInEvent extends AsyncPacketEvent {

    public AsyncPacketInEvent(Channel channel, Packet<?> packet, @Nullable ServerPlayer sender) {
        super(channel, packet, sender);
    }
    
}

