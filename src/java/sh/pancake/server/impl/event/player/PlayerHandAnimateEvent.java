/*
 * Created on Mon Aug 23 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

public class PlayerHandAnimateEvent extends ServerPlayerEventImpl {

    private InteractionHand hand;

    public PlayerHandAnimateEvent(ServerPlayer player, InteractionHand hand) {
        super(player);
        this.hand = hand;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public void setHand(InteractionHand hand) {
        this.hand = hand;
    }


    
}
