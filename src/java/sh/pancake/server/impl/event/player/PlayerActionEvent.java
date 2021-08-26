/*
 * Created on Sat Oct 10 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;

/* 
 *
 * PlayerActionEvent
 * This is base class for certain action events
 * @see ServerboundPlayerActionPacket$Action for actions
 * 
 */
public abstract class PlayerActionEvent extends ServerPlayerEventImpl {

    private ServerboundPlayerActionPacket.Action action;

    public PlayerActionEvent(ServerPlayer player, ServerboundPlayerActionPacket.Action action) {
        super(player);
        this.action = action;
    }

    public ServerboundPlayerActionPacket.Action getAction() {
        return action;
    }

    public void setAction(ServerboundPlayerActionPacket.Action action) {
        this.action = action;
    }

}
