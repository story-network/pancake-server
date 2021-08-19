/*
 * Created on Wed Aug 11 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network.hook;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import io.netty.channel.ChannelFuture;
import net.minecraft.server.network.ServerConnectionListener;
import sh.pancake.server.mixin.accessor.ServerConnectionListenerAccessor;

/**
 * Utility for adding custom ChannelHandler to ServerConnectionListener.
 * 
 * Object must be closed so there is no hooker left to ServerConnectionListener.
 */
public class ServerNetworkHook implements Closeable {

    private final ServerConnectionListener serverConnectionListener;
    private final IncomingListener listener;
    
    private final Map<ChannelFuture, IncomingChannelHook> channelHookMap;

    public ServerNetworkHook(
        ServerConnectionListener serverConnectionListener,
        IncomingListener listener
    ) {
        this.serverConnectionListener = serverConnectionListener;
        this.listener = listener;

        this.channelHookMap = new WeakHashMap<>();

        updateHandler();
    }

    private List<ChannelFuture> getFutures() {
        return ((ServerConnectionListenerAccessor) serverConnectionListener).getChannels();
    }

    private IncomingChannelHook getChannelHookFor(ChannelFuture future) {
        return channelHookMap.computeIfAbsent(future, (key) -> new IncomingChannelHook(key, listener));
    }

    /**
     * Update every IncomingChannelHook
     */
    public void updateHandler() {
        for (var future : getFutures()) {
            getChannelHookFor(future);
        }
    }

    @Override
    public void close() {
        for (var hook : channelHookMap.values()) {
            hook.close();
        }
    }

}
