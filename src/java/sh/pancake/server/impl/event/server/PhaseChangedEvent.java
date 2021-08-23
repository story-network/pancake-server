/*
 * Created on Thu Aug 19 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.server;

import sh.pancake.server.ServerPhase;

public class PhaseChangedEvent {

    private final ServerPhase last;
    private final ServerPhase current;

    public PhaseChangedEvent(ServerPhase last, ServerPhase current) {
        this.last = last;
        this.current = current;
    }

    public ServerPhase getLast() {
        return last;
    }

    public ServerPhase getCurrent() {
        return current;
    }
    
}
