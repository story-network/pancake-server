/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.event;

public class EventCancellable implements IEventCancellable {
    
    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean flag) {
        if (this.cancelled == flag) return;
        this.cancelled = flag;
    }

}
