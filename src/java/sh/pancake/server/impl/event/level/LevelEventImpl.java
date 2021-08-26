/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.level;

import net.minecraft.world.level.Level;
import sh.pancake.server.impl.event.CancellableEvent;

public abstract class LevelEventImpl extends CancellableEvent implements LevelEvent {

    private final Level level;

    public LevelEventImpl(Level level) {
        this.level = level;
    }

    @Override
    public Level getLevel() {
        return level;
    }

}
