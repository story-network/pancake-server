/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.network;

import java.io.IOException;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

@FunctionalInterface
public interface IPacketSerializer {
    
    void writeTo(FriendlyByteBuf bytebuf, Integer packetId, Packet<?> packet) throws IOException;

}
