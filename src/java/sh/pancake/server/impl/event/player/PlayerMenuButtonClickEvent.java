/*
 * Created on Mon Aug 30 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class PlayerMenuButtonClickEvent extends ServerPlayerEventImpl implements MenuEvent<AbstractContainerMenu> {

    private final AbstractContainerMenu menu;

    private int buttonId;

    public PlayerMenuButtonClickEvent(ServerPlayer player, AbstractContainerMenu menu, int buttonId) {
        super(player);
        
        this.menu = menu;
        this.buttonId = buttonId;
    }

    @Override
    public AbstractContainerMenu getMenu() {
        return menu;
    }

    public int getButtonId() {
        return buttonId;
    }

    public void setButtonId(int buttonId) {
        this.buttonId = buttonId;
    }
    
}
