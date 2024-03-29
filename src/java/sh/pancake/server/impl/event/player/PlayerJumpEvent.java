/*
 * Created on Fri Oct 23 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;

/**
 * PlayerJumpEvent
 * 
 * Called only when client send input.
 * Cancelling this event only cancel server side behavior
 * 
 */
public class PlayerJumpEvent extends ServerPlayerEventImpl {

    public PlayerJumpEvent(ServerPlayer player) {
        super(player);
    }

}
