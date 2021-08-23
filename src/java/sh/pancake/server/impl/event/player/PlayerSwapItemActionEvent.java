/*
 * Created on Sat Oct 10 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Only invoked when player preessed shortcut key (default: F)
 */
public class PlayerSwapItemActionEvent extends PlayerActionEvent {

    private ItemStack mainHandItem;
    private ItemStack offHandItem;

    public PlayerSwapItemActionEvent(ServerPlayer player, Action action, ItemStack mainHandItem, ItemStack offHandItem) {
        super(player, action);

        this.mainHandItem = mainHandItem;
        this.offHandItem = offHandItem;
    }

    public ItemStack getMainHandItem() {
        return mainHandItem;
    }

    public void setMainHandItem(ItemStack mainHandItem) {
        this.mainHandItem = mainHandItem;
    }

    public ItemStack getOffHandItem() {
        return offHandItem;
    }

    public void setOffHandItem(ItemStack offHandItem) {
        this.offHandItem = offHandItem;
    }
    
}
