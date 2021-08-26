/*
 * Created on Thu Aug 26 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.entity;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.impl.event.entity.EntityHurtEvent;
import sh.pancake.server.mixin_deco.entity.EntityHurt;

@Mixin(value = { Boat.class, AbstractMinecart.class, LivingEntity.class, ArmorStand.class, ItemEntity.class, HangingEntity.class })
@Implements(@Interface(iface = EntityHurt.class, prefix = "hurt$"))
public abstract class EntitiesMixin {

    @Shadow(remap = false)
    public abstract boolean hurt(DamageSource source, float amount);
    
    @Intrinsic(displace = true)
    public boolean hurt$hurt(DamageSource source, float amount) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            return hurt(source, amount);
        }

        EntityHurtEvent event = new EntityHurtEvent((Entity) (Object) this, source, amount);
        server.dispatchEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        return hurt(event.getSource(), event.getAmount());
    }

}
