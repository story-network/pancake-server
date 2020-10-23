/*
 * Created on Fri Oct 23 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;

/*
 * PlayerJumpEvent
 * 
 * Called only when client send input
 * 
 */
public class PlayerJumpEvent extends PlayerEvent {

    public PlayerJumpEvent(ServerPlayer player) {
        super(player);
    }
    
}
