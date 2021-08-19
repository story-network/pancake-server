/*
 * Created on Thu Aug 12 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network;

import java.io.Closeable;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.server.network.ServerConnectionListener;
import sh.pancake.server.event.EventDispatcher;
import sh.pancake.server.network.hook.ServerNetworkHook;

public class ServerNetworkManager implements Closeable {

    private static final String INCOMING_HANDLER_NAME = "pancake_incoming_hook";
    private static final String OUTGOING_HANDLER_NAME = "pancake_outgoing_hook";

    private final ServerNetworkHook globalHook;

    private final PacketIncomingHandler incomingHandler;
    private final PacketOutgoingHandler outgoingHandler;

    private final Set<Channel> channels;

    public ServerNetworkManager(EventDispatcher dispatcher, ServerConnectionListener serverConnectionListener) {
        this.globalHook = new ServerNetworkHook(serverConnectionListener, this::handleConnection);

        this.incomingHandler = new PacketIncomingHandler(dispatcher);
        this.outgoingHandler = new PacketOutgoingHandler(dispatcher);

        this.channels = Collections.newSetFromMap(new WeakHashMap<>());
    }

    public Set<Channel> getChannels() {
        return Collections.unmodifiableSet(channels);
    }

    public ServerNetworkHook getGlobalHook() {
        return globalHook;
    }

    private void handleConnection(Channel channel) {
        channel.pipeline().addBefore("decoder", INCOMING_HANDLER_NAME, incomingHandler);
        channel.pipeline().addBefore("encoder", OUTGOING_HANDLER_NAME, outgoingHandler);

        channels.add(channel);
    }

    @Override
    public void close() {
        globalHook.close();

        for (var channel : channels) {
            ChannelPipeline pipeline = channel.pipeline();

            if (pipeline.names().contains(INCOMING_HANDLER_NAME)) {
                pipeline.remove(INCOMING_HANDLER_NAME);
            }

            if (pipeline.names().contains(OUTGOING_HANDLER_NAME)) {
                pipeline.remove(OUTGOING_HANDLER_NAME);
            }
        }
    }

}
