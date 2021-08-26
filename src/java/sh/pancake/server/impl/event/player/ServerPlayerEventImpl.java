/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import sh.pancake.server.impl.event.CancellableEvent;
import sh.pancake.server.impl.event.level.ServerLevelEvent;

public abstract class ServerPlayerEventImpl extends CancellableEvent implements ServerPlayerEvent, ServerLevelEvent {
    
    private final ServerPlayer player;

    public ServerPlayerEventImpl(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public ServerPlayer getPlayer() {
        return player;
    }

    @Override
    public ServerLevel getLevel() {
        return player.getLevel();
    }

}
