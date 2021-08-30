/*
 * Created on Mon Aug 30 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.server;

public class ServerReloadEvent {

    private final boolean fullReload;

    public ServerReloadEvent(boolean fullReload) {
        this.fullReload = fullReload;
    }

    public boolean isFullReload() {
        return fullReload;
    }
    
}
