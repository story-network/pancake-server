/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;
import sh.pancake.server.impl.event.CancellableEvent;

public abstract class ServerPlayerEventImpl extends CancellableEvent implements ServerPlayerEvent {
    
    private final ServerPlayer player;

    public ServerPlayerEventImpl(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public ServerPlayer getPlayer() {
        return player;
    }

}
