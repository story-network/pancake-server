/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.status;

import net.minecraft.network.Connection;

public class ServerPingRequestEvent extends StatusEvent {
    
    // Request sent time
    private long time;

    public ServerPingRequestEvent(Connection connection, long time) {
        super(connection);

        this.time = time;
    }

    public long getTime() {
        return time;
    }

}
