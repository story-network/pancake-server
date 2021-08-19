/*
 * Created on Thu Aug 12 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.network.hook;

import io.netty.channel.Channel;

@FunctionalInterface
public interface IncomingListener {

    void handleConnection(Channel channel) throws Exception;
    
}
