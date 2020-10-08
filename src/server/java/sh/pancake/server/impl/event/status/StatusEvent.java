/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.status;

import net.minecraft.network.Connection;
import sh.pancake.server.event.IEvent;

public abstract class StatusEvent implements IEvent {
    
    private Connection connection;

    public StatusEvent(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

}
