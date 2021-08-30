/*
 * Created on Fri Aug 27 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import sh.pancake.server.impl.event.CancellableEvent;

public class PlayerClickMenuEvent extends CancellableEvent implements PlayerEvent, MenuEvent<AbstractContainerMenu> {

    private final AbstractContainerMenu menu;

    private int slot;
    private int state;

    private ClickType clickType;

    private Player player;

    public PlayerClickMenuEvent(AbstractContainerMenu menu, int slot, int state, ClickType clickType, Player player) {
        this.menu = menu;
        this.slot = slot;
        this.state = state;
        this.clickType = clickType;
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return player;
    }
    
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public AbstractContainerMenu getMenu() {
        return menu;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public void setClickType(ClickType clickType) {
        this.clickType = clickType;
    }
}
