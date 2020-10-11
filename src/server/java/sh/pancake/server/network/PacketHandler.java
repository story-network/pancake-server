/*
 * Created on Sun Oct 11 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.network.protocol.Packet;

public class PacketHandler<T extends Packet<?>> {

    private List<IPacketInListener> inListenerList;
    private List<IPacketOutListener> outListenerList;

    public PacketHandler() {
        this.inListenerList = new ArrayList<>();
        this.outListenerList = new ArrayList<>();
    }

    public boolean isRegistered(IPacketOutListener listener) {
        return outListenerList.contains(listener);
    }

    public void register(IPacketOutListener listener) {
        outListenerList.add(listener);
    }

    public void unregister(IPacketOutListener listener) {
        outListenerList.remove(listener);
    }

    public Iterator<IPacketOutListener> getOutIterator() {
        return outListenerList.iterator();
    }

    public boolean isRegistered(IPacketInListener listener) {
        return inListenerList.contains(listener);
    }

    public void register(IPacketInListener listener) {
        inListenerList.add(listener);
    }

    public void unregister(IPacketInListener listener) {
        inListenerList.remove(listener);
    }

    public Iterator<IPacketInListener> getInIterator() {
        return inListenerList.iterator();
    }

}
