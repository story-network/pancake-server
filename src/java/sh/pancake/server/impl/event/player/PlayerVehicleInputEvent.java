/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;

/* 
 *
 * This event ONLY invoked for vehicle input. Not for a riding move handling.
 * @see PlayerRideMoveEvent for riding move event.
 * 
 */
public class PlayerVehicleInputEvent extends ServerPlayerEventImpl {

    private float xxa;
    private float zza;

    private boolean jumping;
    private boolean sneaking;

    public PlayerVehicleInputEvent(ServerPlayer player, float xxa, float zza, boolean jumping, boolean sneaking) {
        super(player);

        this.xxa = xxa;
        this.zza = zza;
        this.jumping = jumping;
        this.sneaking = sneaking;
    }

    public float getXxa() {
        return xxa;
    }

    public void setXxa(float xxa) {
        this.xxa = xxa;
    }

    public float getZza() {
        return zza;
    }

    public void setZza(float zza) {
        this.zza = zza;
    }
    
    public boolean isJumping() {
        return jumping;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public boolean isSneaking() {
        return sneaking;
    }

    public void setSneaking(boolean sneaking) {
        this.sneaking = sneaking;
    }
    
}
