/*
 * Created on Wed Aug 11 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.accessor;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import io.netty.channel.ChannelFuture;
import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerConnectionListener;

@Mixin(ServerConnectionListener.class)
public interface ServerConnectionListenerAccessor {
    
    @Accessor("channels")
    List<ChannelFuture> getChannels();

    @Accessor("connections")
    List<Connection> getConnection();
    
}
