/*
 * Created on Fri Aug 27 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class PlayerOpenMenuEvent extends ServerPlayerEventImpl {

    private AbstractContainerMenu menu;

    public PlayerOpenMenuEvent(ServerPlayer player, @Nullable AbstractContainerMenu menu) {
        super(player);

        this.menu = menu;
    }

    @Nullable
    public AbstractContainerMenu getMenu() {
        return menu;
    }

    public void setMenu(@Nullable AbstractContainerMenu menu) {
        this.menu = menu;
    }

}
