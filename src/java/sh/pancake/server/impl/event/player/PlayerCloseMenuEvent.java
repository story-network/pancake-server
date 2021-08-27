/*
 * Created on Fri Aug 27 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;

public class PlayerCloseMenuEvent extends ServerPlayerEventImpl {

    private final boolean forced;

    public PlayerCloseMenuEvent(ServerPlayer player, boolean forced) {
        super(player);

        this.forced = forced;
    }
    
    public boolean isForced() {
        return forced;
    }

}
