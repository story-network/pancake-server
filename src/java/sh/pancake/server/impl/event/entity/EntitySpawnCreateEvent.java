/*
 * Created on Thu Aug 26 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.entity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import sh.pancake.server.impl.event.CancellableEvent;
import sh.pancake.server.impl.event.level.ServerLevelEvent;

public class EntitySpawnCreateEvent extends CancellableEvent implements ServerLevelEvent {

    private final ServerLevel level;

    private EntityType<?> type;

    private CompoundTag nbtTag;
    private Component customName;
    private BlockPos position;

    private MobSpawnType spawnType;

    private Player summoner;

    public EntitySpawnCreateEvent(
        ServerLevel level,
        EntityType<?> type,
        @Nullable CompoundTag nbtTag,
        @Nullable Component customName,
        BlockPos position,
        MobSpawnType spawnType,
        @Nullable Player summoner
    ) {
        this.level = level;
        this.type = type;
        this.nbtTag = nbtTag;
        this.customName = customName;
        this.position = position;
        this.spawnType = spawnType;
        this.summoner = summoner;
    }

    @Override
    public ServerLevel getLevel() {
        return level;
    }

    public EntityType<?> getType() {
        return type;
    }

    public void setType(EntityType<?> type) {
        this.type = type;
    }

    @Nullable
    public CompoundTag getNbtTag() {
        return nbtTag;
    }

    public void setNbtTag(@Nullable CompoundTag nbtTag) {
        this.nbtTag = nbtTag;
    }

    @Nullable
    public Component getCustomName() {
        return customName;
    }

    @Nullable
    public void setCustomName(Component customName) {
        this.customName = customName;
    }

    public BlockPos getPosition() {
        return position;
    }

    public void setPosition(BlockPos position) {
        this.position = position;
    }

    public MobSpawnType getSpawnType() {
        return spawnType;
    }

    public void setSpawnType(MobSpawnType spawnType) {
        this.spawnType = spawnType;
    }

    @Nullable
    public Player getSummoner() {
        return summoner;
    }

    public void setSummoner(@Nullable Player summoner) {
        this.summoner = summoner;
    }
    
}
