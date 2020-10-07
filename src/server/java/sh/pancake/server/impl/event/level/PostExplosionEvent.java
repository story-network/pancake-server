/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.level;

import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.phys.Vec3;

public class PostExplosionEvent extends AbstractExplosionEvent {

    private List<BlockPos> blockList;

    private Map<Player, Vec3> hitPlayerMap;

    public PostExplosionEvent(Level level, Explosion explosion, double x, double y, double z, float radius, boolean fire, DamageSource damageSource,
            Entity entity, BlockInteraction interaction, List<BlockPos> blockList, Map<Player, Vec3> hitPlayerMap) {
        super(level, explosion, x, y, z, radius, fire, damageSource, entity, interaction);

        this.blockList = blockList;
        this.hitPlayerMap = hitPlayerMap;
    }

    public List<BlockPos> getBlockList() {
        return blockList;
    }

    public Map<Player, Vec3> getHitPlayerMap() {
        return hitPlayerMap;
    }
    
}
