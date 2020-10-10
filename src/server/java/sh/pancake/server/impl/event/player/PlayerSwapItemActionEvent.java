/*
 * Created on Sat Oct 10 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.server.level.ServerPlayer;

public class PlayerSwapItemActionEvent extends PlayerActionEvent {

    public PlayerSwapItemActionEvent(ServerPlayer player, Action action) {
        super(player, action);
    }
    
}
