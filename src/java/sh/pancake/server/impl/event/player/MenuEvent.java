/*
 * Created on Mon Aug 30 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.world.inventory.AbstractContainerMenu;

public interface MenuEvent<T extends AbstractContainerMenu> {

    T getMenu();
    
}
