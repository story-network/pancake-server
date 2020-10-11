/*
 * Created on Sat Oct 10 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.server.level.ServerPlayer;

public class AsyncPlayerResourcePackStatusEvent extends PlayerEvent {

    private Status status;

    public AsyncPlayerResourcePackStatusEvent(ServerPlayer player, Status status) {
        super(player);
        
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public static enum Status {

        ACCEPTED,
        DENIED,

        LOADED,
        DOWNLOAD_FAILED,
        INVALID;

        public static Status fromPacketAction(ServerboundResourcePackPacket.Action action) {
            switch (action) {
                case ACCEPTED: return ACCEPTED;
                case DECLINED: return DENIED;
                case FAILED_DOWNLOAD: return DOWNLOAD_FAILED;
                case SUCCESSFULLY_LOADED: return LOADED;
                default: return INVALID;
            }
        }

    }
}