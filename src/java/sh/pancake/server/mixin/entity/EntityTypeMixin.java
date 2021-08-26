/*
 * Created on Thu Aug 26 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
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
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.impl.event.entity.EntitySpawnCreateEvent;

@Mixin(EntityType.class)
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

}
