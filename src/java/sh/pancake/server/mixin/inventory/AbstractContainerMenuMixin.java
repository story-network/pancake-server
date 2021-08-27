/*
 * Created on Wed Aug 25 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.inventory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.impl.event.player.PlayerClickMenuEvent;
import sh.pancake.server.impl.event.player.PlayerDropItemEvent;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {

    @Shadow
    public abstract void doClick(int slot, int state, ClickType type, Player player);

    @Redirect(
        method = "clicked",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/world/inventory/AbstractContainerMenu.doClick(IILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V"
        )
    )
    public void click_doClick(AbstractContainerMenu container, int slot, int state, ClickType type, Player player) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            doClick(slot, state, type, player);
            return;
        }

        PlayerClickMenuEvent event = new PlayerClickMenuEvent(container, slot, state, type, player);
        server.dispatchEvent(event);
        if (event.isCancelled()) {
            return;
        }

        doClick(event.getSlot(), event.getState(), event.getClickType(), event.getPlayer());
    }
    
    @Redirect(
        method = "doClick",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/world/entity/player/Player.drop(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;"
        )
    )
    public ItemEntity doClick_drop(Player player, ItemStack item, boolean dropAll, int slot, int state, ClickType type) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            return player.drop(item, dropAll);
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;

        PlayerDropItemEvent event = new PlayerDropItemEvent(
            serverPlayer,
            item,
            dropAll,
            PlayerDropItemEvent.Source.INVENTORY
        );
        server.dispatchEvent(event);

        if (event.isCancelled()) {
            AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;

            if (type == ClickType.THROW) {
                // shortcut drop cancellation
                menu.getSlot(slot).safeInsert(item, item.getCount());
            } else if (slot == AbstractContainerMenu.SLOT_CLICKED_OUTSIDE) {
                if (menu.getCarried() != item) {
                    // right click drop cancellation
                    if (menu.getCarried().sameItem(item)) {
                        menu.getCarried().grow(item.getCount());
                        menu.setRemoteCarried(ItemStack.EMPTY);
                    }
                } else {
                    // left click drop cancellation
                    serverPlayer.server.execute(() -> {
                        menu.setCarried(item);
                    });
                    menu.setRemoteCarried(ItemStack.EMPTY);
                }
            }
            
            menu.broadcastChanges();

            return null;
        }

        return player.drop(event.getDropItem(), event.isDropAll());
    }

}
