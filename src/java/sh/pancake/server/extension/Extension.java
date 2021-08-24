/*
 * Created on Thu Aug 12 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.extension;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.minecraft.resources.ResourceLocation;
import sh.pancake.server.command.PancakeCommandDispatcher;
import sh.pancake.server.command.PancakeCommandStack;
import sh.pancake.server.event.EventManager;
import sh.pancake.server.network.payload.PayloadChannel;
import sh.pancake.server.network.payload.PayloadCollector;
import sh.pancake.server.network.payload.GlobalPayloadListener;

public class Extension<T> implements PayloadCollector, GlobalPayloadListener {

    private final String id;

    private final T metadata;

    private final List<String> dependencies;
    
    private final URL url;

    private final EventManager eventManager;
    private final PancakeCommandDispatcher<PancakeCommandStack> commandDispatcher;

    private final Map<ResourceLocation, PayloadChannel> payloadMap;

    public Extension(String id, URL url, List<String> dependencies, T metadata) {
        this.id = id;

        this.metadata = metadata;

        this.dependencies = dependencies;

        this.url = url;

        this.eventManager = new EventManager();
        this.commandDispatcher = new PancakeCommandDispatcher<>(id);

        this.payloadMap = new HashMap<>();
    }

    public Collection<String> getDependencies() {
        return Collections.unmodifiableCollection(dependencies);
    }
    
    public T getMetadata() {
        return metadata;
    }

    public String getId() {
        return id;
    }
    
    public URL getURL() {
        return url;
    }

    public EventManager getEventManager() {
        return eventManager;
    }
    
    public PancakeCommandDispatcher<PancakeCommandStack> getCommandDispatcher() {
        return commandDispatcher;
    }

    public PayloadChannel registerChannel(String identifier) {
        return registerChannel(new ResourceLocation(identifier));
    }

    public PayloadChannel registerChannel(ResourceLocation identifier) {
        return payloadMap.computeIfAbsent(identifier, (key) -> new PayloadChannel(key));
    }

    public boolean unregisterChannel(String identifier) {
        return unregisterChannel(new ResourceLocation(identifier));
    }

    public boolean unregisterChannel(ResourceLocation identifier) {
        return payloadMap.remove(identifier) != null;
    }

    public Set<ResourceLocation> getPayloadChannels() {
        return payloadMap.keySet();
    }

    @Override
    public void processPayload(ResourceLocation identifier, Channel channel, ByteBuf buf) {
        if (!payloadMap.containsKey(identifier)) return;

        payloadMap.get(identifier).recvPayloadData(channel, buf);
    }

    @Override
    public void fillPayloadChannels(Set<ResourceLocation> set) {
        set.addAll(payloadMap.keySet());
    }
    
}
