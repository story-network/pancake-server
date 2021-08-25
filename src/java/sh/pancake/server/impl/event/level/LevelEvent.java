/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.level;

import net.minecraft.server.level.ServerLevel;
import sh.pancake.server.impl.event.CancellableEvent;

public abstract class LevelEvent extends CancellableEvent {

    private final ServerLevel level;

    public LevelEvent(ServerLevel level) {
        this.level = level;
    }

    public ServerLevel getLevel() {
        return level;
    }

}
