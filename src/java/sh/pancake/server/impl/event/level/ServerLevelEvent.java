/*
 * Created on Thu Aug 26 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.level;

import net.minecraft.server.level.ServerLevel;

public interface ServerLevelEvent extends LevelEvent {

    ServerLevel getLevel();

}
