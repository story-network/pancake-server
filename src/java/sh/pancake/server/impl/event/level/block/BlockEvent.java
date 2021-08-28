/*
 * Created on Sat Aug 28 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.level.block;

import net.minecraft.world.level.block.state.BlockState;
import sh.pancake.server.impl.event.level.LevelEvent;

public interface BlockEvent extends LevelEvent {

    BlockState getBlockState();

}
