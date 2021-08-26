/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.level;

import net.minecraft.server.level.ServerLevel;
import sh.pancake.server.impl.event.CancellableEvent;

public abstract class ServerLevelEventImpl extends CancellableEvent implements ServerLevelEvent {

    private final ServerLevel level;

    public ServerLevelEventImpl(ServerLevel level) {
        this.level = level;
    }

    @Override
    public ServerLevel getLevel() {
        return level;
    }

}
