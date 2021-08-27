/*
 * Created on Fri Aug 27 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.impl.event.player.PlayerCloseMenuEvent;
import sh.pancake.server.impl.event.player.PlayerOpenMenuPreEvent;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    
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

}
