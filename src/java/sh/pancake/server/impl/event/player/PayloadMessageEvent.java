/*
 * Created on Sat Aug 21 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import io.netty.channel.Channel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import sh.pancake.server.impl.event.network.ChannelEvent;

public class PayloadMessageEvent extends ServerPlayerEventImpl implements ChannelEvent {

    private final Channel channel;

    private ResourceLocation identifier;
    private final FriendlyByteBuf data;

    public PayloadMessageEvent(ServerPlayer player, Channel channel, ResourceLocation identifier, FriendlyByteBuf data) {
        super(player);
        this.channel = channel;
        this.identifier = identifier;
        this.data = data;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    public ResourceLocation getIdentifier() {
        return identifier;
    }

    public void setIdentifier(ResourceLocation identifier) {
        this.identifier = identifier;
    }

    public FriendlyByteBuf getData() {
        return data;
    }
    
}
