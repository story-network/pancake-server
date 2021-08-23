/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */
package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;

// Note: cancelling or changing the flag can cause the result being desync with client
public class PlayerToggleSprintEvent extends PlayerEvent {

    private boolean sprinting;

    public PlayerToggleSprintEvent(ServerPlayer player, boolean sprinting) {
        super(player);

        this.sprinting = sprinting;
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }

}
