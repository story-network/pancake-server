/*
 * Created on Sat Oct 10 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.server.level.ServerPlayer;

public class PlayerFinishActionEvent extends PlayerActionEvent {

    public PlayerFinishActionEvent(ServerPlayer player, Action action) {
        super(player, action);
    }
    
}
