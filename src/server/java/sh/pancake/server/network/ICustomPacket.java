/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;

// Define custom packet that can be serialized into certain type of packet
public interface ICustomPacket<T extends PacketListener> extends Packet<T> {
    
    // Return empty packet object for type
    Packet<?> getTargetPacket();

}
