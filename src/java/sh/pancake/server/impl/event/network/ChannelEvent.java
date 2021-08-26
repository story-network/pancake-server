/*
 * Created on Thu Aug 26 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.network;

import io.netty.channel.Channel;

public interface ChannelEvent {
    
    Channel getChannel();

}
