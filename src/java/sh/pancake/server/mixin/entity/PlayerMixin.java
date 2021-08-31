/*
 * Created on Tue Aug 31 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.entity;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.impl.event.player.PlayerUseItemEvent;
import sh.pancake.server.impl.player.EntityInteractInfo;
import sh.pancake.server.mixin_deco.entity.PlayerInteractOn;

@Mixin(Player.class)
@Implements(@Interface(iface = PlayerInteractOn.class, prefix = "interact$"))
public abstract class PlayerMixin {

    @Shadow(remap = false)
    public abstract InteractionResult interactOn(Entity entity, InteractionHand hand);

    @Intrinsic(displace = true)
    public InteractionResult interact$interactOn(Entity entity, InteractionHand hand) {
        if (!((Object) this instanceof ServerPlayer)) {
            return interactOn(entity, hand);
        }

        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            return interactOn(entity, hand);
        }

        ServerPlayer player = (ServerPlayer) (Object) this;

        PlayerUseItemEvent event = new PlayerUseItemEvent(player, hand, new EntityInteractInfo(entity));
        server.dispatchEvent(event);

        if (event.isCancelled()) {
            return event.getCancelledResult();
        }

        return interactOn(entity, hand);
    }
    
}
