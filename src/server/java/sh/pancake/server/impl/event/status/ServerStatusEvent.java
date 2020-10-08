/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.status;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.status.ServerStatus;

public class ServerStatusEvent extends StatusEvent {

    private ServerStatus status;

    public ServerStatusEvent(Connection connection, ServerStatus status) {
        super(connection);
        
        this.status = status;
    }

    public ServerStatus getStatus() {
        return status;
    }

    public void setStatus(ServerStatus status) {
        this.status = status;
    }

}
