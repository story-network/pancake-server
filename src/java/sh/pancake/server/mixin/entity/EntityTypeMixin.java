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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.impl.event.entity.EntityCreateEvent;
import sh.pancake.server.impl.event.entity.EntitySpawnCreateEvent;
import sh.pancake.server.mixin_deco.entity.EntityTypeCreate;

@Mixin(EntityType.class)
@Implements(@Interface(iface = EntityTypeCreate.class, prefix = "create$"))
public abstract class EntityTypeMixin {
    
    @Redirect(
        method = "spawn(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/network/chat/Component;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/MobSpawnType;ZZ)Lnet/minecraft/world/entity/Entity;",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/world/entity/EntityType.create(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/network/chat/Component;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/MobSpawnType;ZZ)Lnet/minecraft/world/entity/Entity;"
        )
    )
    public Entity spawn_create(
        EntityType<?> type,
        ServerLevel level,
        CompoundTag tag,
        Component customName,
        Player summoner,
        BlockPos position,
        MobSpawnType spawnType,
        boolean unknown1,
        boolean unknown2
    ) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            return type.create(level, tag, customName, summoner, position, spawnType, unknown1, unknown2);
        }

        EntitySpawnCreateEvent event = new EntitySpawnCreateEvent(level, type, tag, customName, position, spawnType, summoner);
        server.dispatchEvent(event);

        if (event.isCancelled()) {
            return null;
        }

        return event.getType().create(
            event.getLevel(),
            event.getNbtTag(),
            event.getCustomName(),
            event.getSummoner(),
            event.getPosition(),
            event.getSpawnType(),
            unknown1,
            unknown2
        );

    }

    @Shadow(remap = false)
    public abstract Entity create(Level level);

    @Intrinsic(displace = true)
    public Entity create$create(Level level) {
        Entity entity = create(level);

        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) {
            return entity;
        }

        EntityCreateEvent event = new EntityCreateEvent(entity);
        server.dispatchEvent(event);
        if (event.isCancelled()) {
            entity.discard();
            return null;
        }

        return entity;
    }

}
