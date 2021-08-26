/*
 * Created on Mon Aug 23 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;

public class PlayerPaddleBoatEvent extends ServerPlayerEventImpl {

    private boolean left;
    private boolean right;

    public PlayerPaddleBoatEvent(ServerPlayer player, boolean left, boolean right) {
        super(player);

        this.left = left;
        this.right = right;
    }

    public boolean getLeft() {
        return left;
    }

    public boolean getRight() {
        return right;
    }
    
    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }
}
