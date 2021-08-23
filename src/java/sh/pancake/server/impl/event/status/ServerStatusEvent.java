/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.status;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.status.ServerStatus;
import sh.pancake.server.impl.event.CancellableEvent;

public class ServerStatusEvent extends CancellableEvent {

    private final Connection connection;

    private ServerStatus status;

    public ServerStatusEvent(Connection connection, ServerStatus status) {
        this.connection = connection;
        this.status = status;
    }

    public Connection getConnection() {
        return connection;
    }

    public ServerStatus getStatus() {
        return status;
    }

    public void setStatus(ServerStatus status) {
        this.status = status;
    }

}
