/*
 * Created on Tue Aug 31 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import sh.pancake.server.mixin.accessor.MobAccessor;

public class MobUtil {

    public static GoalSelector getGoalSelector(Mob mob) {
        return ((MobAccessor) mob).getGoalSelector();
    }

    public static GoalSelector getTargetSelector(Mob mob) {
        return ((MobAccessor) mob).getTargetSelector();
    }
    
}
