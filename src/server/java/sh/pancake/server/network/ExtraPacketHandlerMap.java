/*
 * Created on Sun Oct 11 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import net.minecraft.network.protocol.Packet;
import sh.pancake.server.ExtraBasedMap;
import sh.pancake.server.IPancakeExtra;

public class ExtraPacketHandlerMap extends ExtraBasedMap<Class<? extends Packet<?>>, PacketHandler<? extends Packet<?>>> {

    @Override
    protected Map<Class<? extends Packet<?>>, PacketHandler<? extends Packet<?>>> createMapFor(IPancakeExtra holder) {
        return new WeakHashMap<>();
    }

    public <R extends Packet<?>>PacketHandler<R> getHandlerFor(IPancakeExtra extra, Class<R> cl) {
        return (PacketHandler<R>) super.computeIfAbsentOf(extra, cl, (clazz) -> new PacketHandler<R>());
    }

    public Collection<PacketHandler<? extends Packet<?>>> getAllHandlerof(Class<?> cl) {
        return valuesMap().stream()
                    .filter((map) -> map.containsKey(cl))
                    .map((map) -> map.get(cl))
                    .collect(Collectors.toList());
    }

}
