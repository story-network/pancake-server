/*
 * Created on Fri Aug 27 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class PlayerOpenMenuEvent extends ServerPlayerEventImpl implements MenuEvent<AbstractContainerMenu> {

    private AbstractContainerMenu menu;

    public PlayerOpenMenuEvent(ServerPlayer player, AbstractContainerMenu menu) {
        super(player);

        this.menu = menu;
    }

    @Override
    public AbstractContainerMenu getMenu() {
        return menu;
    }

    public void setMenu(AbstractContainerMenu menu) {
        this.menu = menu;
    }

}
