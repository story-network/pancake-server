package sh.pancake.server.impl.event.status;

/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

import net.minecraft.network.Connection;
import sh.pancake.server.impl.event.CancellableEvent;

public class ServerPingRequestEvent extends CancellableEvent {

    private final Connection connection;

    private long time;

    public ServerPingRequestEvent(Connection connection, long time) {
        this.connection = connection;
        this.time = time;
    }

    public Connection getConnection() {
        return connection;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
