/*
 * Created on Sun Oct 11 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.network.protocol.Packet;
import sh.pancake.server.ExtraBasedMap;
import sh.pancake.server.IPancakeExtra;

public class ExtraPacketHandlerMap extends ExtraBasedMap<Class<? extends Packet<?>>, PacketHandler<? extends Packet<?>>> {

    @Override
    protected Map<Class<? extends Packet<?>>, PacketHandler<? extends Packet<?>>> createMapFor(IPancakeExtra holder) {
        return new WeakHashMap<>();
    }

}
