/*
 * Created on Fri Oct 23 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;

/*
 * PlayerInputEvent
 * 
 * Called only when client send input
 * 
 */
public class PlayerInputEvent extends PlayerEvent {

    private boolean moved;
    private boolean rotated;
    private boolean onGround;
    
    private double newX;
    private double newY;
    private double newZ;
    
    private float newXRot;
    private float newYRot;

    public PlayerInputEvent(ServerPlayer player,
        boolean moved, boolean rotated, boolean onGround,
        double newX, double newY, double newZ,
        float newXRot, float newYRot) {
        super(player);

        this.moved = moved;
        this.rotated = rotated;
        this.onGround = onGround;

        this.newX = newX;
        this.newY = newY;
        this.newZ = newZ;

        this.newXRot = newXRot;
        this.newYRot = newYRot;
    }

    public boolean isMoved() {
        return moved;
    }

    public boolean isRotated() {
        return rotated;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public double getNewX() {
        return newX;
    }

    public double getNewY() {
        return newY;
    }

    public double getNewZ() {
        return newZ;
    }

    public float getNewXRot() {
        return newXRot;
    }

    public float getNewYRot() {
        return newYRot;
    }
    
}
