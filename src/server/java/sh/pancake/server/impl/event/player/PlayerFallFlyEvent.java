/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;

public class PlayerFallFlyEvent extends PlayerEvent {

    private boolean fallFly;

    public PlayerFallFlyEvent(ServerPlayer player, boolean fallFly) {
        super(player);

        this.fallFly = fallFly;
    }

    public boolean shouldFallFly() {
        return fallFly;
    }

    public void setFallFly(boolean fallFly) {
        this.fallFly = fallFly;
    }
    
}
