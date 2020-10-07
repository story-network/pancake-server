/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.network;

import java.io.IOException;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class DefaultPacketSerializer implements IPacketSerializer {

    @Override
    public void writeTo(FriendlyByteBuf bytebuf, Integer packetId, Packet<?> packet) throws IOException {
        if (packetId == null) {
            throw new IOException("Can't serialize unregistered packet");
        } 

        bytebuf.writeVarInt(packetId);
        packet.write(bytebuf);
    }

}
