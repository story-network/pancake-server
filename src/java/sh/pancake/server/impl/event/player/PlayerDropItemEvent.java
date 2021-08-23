/*
 * Created on Sat Oct 10 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class PlayerDropItemEvent extends PlayerEvent {

    private boolean dropAll;
    private ItemStack dropItem;

    public PlayerDropItemEvent(ServerPlayer player, boolean dropAll, @Nullable ItemStack dropItem) {
        super(player);

        this.dropAll = dropAll;
    }

	public boolean isDropAll() {
        return dropAll;
    }

    public void setDropAll(boolean dropAll) {
        this.dropAll = dropAll;
    }
    
    @Nullable
    public ItemStack getDropItem() {
        return dropItem;
    }

    public void setDropItem(@Nullable ItemStack dropItem) {
        this.dropItem = dropItem;
    }
    
}
