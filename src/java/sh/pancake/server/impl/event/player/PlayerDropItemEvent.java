/*
 * Created on Sat Oct 10 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class PlayerDropItemEvent extends ServerPlayerEventImpl {

    private ItemStack dropItem;
    private boolean dropAll;

    private final Source source;

    public PlayerDropItemEvent(ServerPlayer player, ItemStack dropItem, boolean dropAll, Source source) {
        super(player);

        this.dropItem = dropItem;
        this.dropAll = dropAll;

        this.source = source;
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

    public Source getSource() {
        return source;
    }

    public static enum Source {

        SHORTCUT(false),
        INVENTORY(true),
        CREATIVE_INVENTORY(true);

        private final boolean inventory;

        Source(boolean inventory) {
            this.inventory = inventory;
        }

        public boolean isInventory() {
            return inventory;
        }

    }
    
}
