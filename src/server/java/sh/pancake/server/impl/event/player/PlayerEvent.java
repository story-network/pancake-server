/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import sh.pancake.server.event.EventCancellable;

public abstract class PlayerEvent extends EventCancellable {
    
    private ServerPlayer player;

    public PlayerEvent(ServerPlayer player) {
        this.player = player;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public Level getLevel() {
        return player.getLevel();
    }

}
