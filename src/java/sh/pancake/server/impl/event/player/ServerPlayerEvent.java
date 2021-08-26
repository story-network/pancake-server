/*
 * Created on Thu Aug 26 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;

public interface ServerPlayerEvent extends PlayerEvent {
    
    ServerPlayer getPlayer();

}
