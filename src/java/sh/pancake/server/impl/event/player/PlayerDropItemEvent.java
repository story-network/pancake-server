/*
 * Created on Sat Oct 10 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class PlayerDropItemEvent extends PlayerEvent {

    private ItemStack dropItem;
    private boolean dropAll;

    public PlayerDropItemEvent(ServerPlayer player, ItemStack dropItem, boolean dropAll) {
        super(player);

        this.dropItem = dropItem;
        this.dropAll = dropAll;
    }

	public boolean isDropAll() {
        return dropAll;
    }

    public void setDropAll(boolean dropAll) {
        this.dropAll = dropAll;
    }

    public ItemStack getDropItem() {
        return dropItem;
    }

    public void setDropItem(ItemStack dropItem) {
        this.dropItem = dropItem;
    }
    
}
