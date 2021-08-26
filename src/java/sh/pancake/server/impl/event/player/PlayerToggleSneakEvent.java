/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */
package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;

// Note: cancelling or changing the flag can cause the result being desync with client
public class PlayerToggleSneakEvent extends ServerPlayerEventImpl {

    private boolean sneaking;

    public PlayerToggleSneakEvent(ServerPlayer player, boolean sneaking) {
        super(player);

        this.sneaking = sneaking;
    }

    public boolean isSneaking() {
        return sneaking;
    }

    public void setSneaking(boolean sneaking) {
        this.sneaking = sneaking;
    }

}
