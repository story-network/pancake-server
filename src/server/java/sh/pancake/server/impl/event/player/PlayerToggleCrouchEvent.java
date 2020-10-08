/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */
package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;

// Note: cancelling or changing the flag can cause the result being desync with client
public class PlayerToggleCrouchEvent extends PlayerEvent {

    private boolean crouch;

    public PlayerToggleCrouchEvent(ServerPlayer player, boolean crouch) {
        super(player);

        this.crouch = crouch;
    }

    public boolean shouldCrouch() {
        return crouch;
    }

    public void setCrouch(boolean crouch) {
        this.crouch = crouch;
    }

}
