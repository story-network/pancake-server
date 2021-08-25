/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.level;

import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sh.pancake.server.impl.level.ExplosionInfo;

public class LevelExplosionPostEvent extends LevelEvent {

    private ExplosionInfo info;

    private List<BlockPos> blockList;

    private Map<Player, Vec3> hitPlayerMap;

    public LevelExplosionPostEvent(Level level, ExplosionInfo info, List<BlockPos> blockList, Map<Player, Vec3> hitPlayerMap) {
        super(level);

        this.info = info;

        this.blockList = blockList;
        this.hitPlayerMap = hitPlayerMap;
    }

    public ExplosionInfo getInfo() {
        return info;
    }

    public void setInfo(ExplosionInfo info) {
        this.info = info;
    }

    public List<BlockPos> getBlockList() {
        return blockList;
    }

    public void setBlockList(List<BlockPos> blockList) {
        this.blockList = blockList;
    }

    public Map<Player, Vec3> getHitPlayerMap() {
        return hitPlayerMap;
    }

    public void setHitPlayerMap(Map<Player, Vec3> hitPlayerMap) {
        this.hitPlayerMap = hitPlayerMap;
    }
    
}
