/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.level;

import net.minecraft.world.level.Level;
import sh.pancake.server.impl.event.CancellableEvent;

public abstract class LevelEvent extends CancellableEvent {

    private final Level level;

    public LevelEvent(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

}
