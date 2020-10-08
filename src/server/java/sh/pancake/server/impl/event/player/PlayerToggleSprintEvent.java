/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */
package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;

// Note: cancelling or changing the flag can cause the result being desync with client
public class PlayerToggleSprintEvent extends PlayerEvent {

    private boolean sprint;

    public PlayerToggleSprintEvent(ServerPlayer player, boolean sprint) {
        super(player);

        this.sprint = sprint;
    }

    public boolean shouldSprint() {
        return sprint;
    }

    public void setSprint(boolean sprint) {
        this.sprint = sprint;
    }

}
