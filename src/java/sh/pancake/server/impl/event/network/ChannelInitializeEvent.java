
/*
 * Created on Fri Aug 20 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.network;

import io.netty.channel.Channel;

public class ChannelInitializeEvent implements ChannelEvent {
    
    private final Channel channel;

    public ChannelInitializeEvent(Channel channel) {
        this.channel = channel;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

}
