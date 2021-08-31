/*
 * Created on Fri Aug 27 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.entity;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.impl.event.player.PlayerCloseMenuEvent;
import sh.pancake.server.impl.event.player.PlayerInteractEvent;
import sh.pancake.server.impl.event.player.PlayerOpenMenuPreEvent;
import sh.pancake.server.impl.player.EntityInteractInfo;
import sh.pancake.server.mixin_deco.entity.ServerPlayerAttack;

@Mixin(ServerPlayer.class)
@Implements(@Interface(iface = ServerPlayerAttack.class, prefix = "attack$"))
public abstract class ServerPlayerMixin {
    
    @ModifyVariable(method = "openMenu", at = @At("HEAD"), argsOnly = true)
    public MenuProvider openMenuPre(MenuProvider provider) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            return provider;
        }

        PlayerOpenMenuPreEvent event = new PlayerOpenMenuPreEvent((ServerPlayer) (Object) this, provider);
        server.dispatchEvent(event);

        return event.getProvider();
    }

    @Inject(method = "closeContainer", at = @At("HEAD"))
    public void closeContainerPre(CallbackInfo info) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            return;
        }

        PlayerCloseMenuEvent event = new PlayerCloseMenuEvent((ServerPlayer) (Object) this, true);
        server.dispatchEvent(event);
        if (event.isCancelled()) {
            info.cancel();
            return;
        }
    }

    @Shadow(remap = false)
    public abstract void attack(Entity entity);

    @Intrinsic(displace = true)
    public void attack$attack(Entity entity) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            attack(entity);
            return;
        }

        ServerPlayer player = (ServerPlayer) (Object) this;

        PlayerInteractEvent event = new PlayerInteractEvent(player, player.swingingArm, new EntityInteractInfo(entity));
        server.dispatchEvent(event);
        if (event.isCancelled()) {
            return;
        }

        attack(event.getEntityInteractInfo().getEntity());
    }

}
